#!/usr/bin/env python3
"""Reproducible, standard-library-only prayer source importer and Kotlin compiler."""
from __future__ import annotations
import argparse
import hashlib
from email.utils import parsedate_to_datetime
from datetime import timezone
import json
import re
import sys
import time
import unicodedata
import urllib.error
import urllib.parse
import urllib.request
import urllib.robotparser
from html.parser import HTMLParser
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONTENT = ROOT / 'content/treasury'
OUTPUT = ROOT / 'app/src/main/java/com/sertumrosarum/domain/treasury/GeneratedTreasury.kt'
AGENT = 'SertumRosarumContent/1.0'
LIMIT = 2 * 1024 * 1024
CATEGORIES = {'WAKING', 'NIGHT', 'DISTRESS', 'THANKSGIVING', 'COMMUNION', 'MARIAN'}


def normalized(text):
    return ' '.join(unicodedata.normalize('NFC', text).replace('\xa0', ' ').split())


class Paragraphs(HTMLParser):
    """Preserve paragraph and verse boundaries, excluding executable/hidden markup."""
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.rows = []
        self.current = None
        self.title = []
        self.bold = 0
        self.ignored = 0

    def handle_starttag(self, tag, attrs):
        if tag in ('script', 'style'): self.ignored += 1
        if self.ignored: return
        if tag == 'p': self.flush(); self.current = []; self.title = []
        if tag in ('b', 'strong'): self.bold += 1
        if tag == 'br' and self.current is not None: self.current.append('\n')

    def handle_endtag(self, tag):
        if tag in ('script', 'style'): self.ignored = max(0, self.ignored - 1); return
        if self.ignored: return
        if tag in ('b', 'strong'): self.bold = max(0, self.bold - 1)
        if tag == 'p': self.flush()

    def handle_data(self, data):
        if self.ignored: return
        if self.current is not None:
            self.current.append(data)
            if self.bold: self.title.append(data)

    def flush(self):
        if self.current is not None:
            text = '\n'.join(filter(None, (normalized(s) for s in ''.join(self.current).splitlines())))
            if text: self.rows.append({'heading': normalized(''.join(self.title)), 'text': text})
        self.current = None


def extract(html):
    parser = Paragraphs(); parser.feed(html); parser.flush()
    starts = [i for i, row in enumerate(parser.rows) if row['text'] == 'A) ORAÇÕES COMUNS']
    if not starts: raise ValueError('Common-prayer appendix marker missing; source layout changed')
    rows = parser.rows[starts[-1] + 1:]
    end = next((i for i, r in enumerate(rows) if r['text'] == 'B) FÓRMULAS DE DOUTRINA CATÓLICA'), None)
    if end is None: raise ValueError('Appendix end marker missing; refusing a partial extraction')
    rows = rows[:end]
    if len(rows) < 90 or not any(r['heading'] == 'Anima Christi' for r in rows):
        raise ValueError('Expected prayer sections missing; review upstream changes')
    return rows


def read_json(path): return json.loads(path.read_text(encoding='utf-8'))
def encoded(value): return json.dumps(value, ensure_ascii=False, indent=2) + '\n'
def digest(data): return hashlib.sha256(data).hexdigest()


def atomic_write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    temporary = path.with_suffix(path.suffix + '.tmp')
    temporary.write_bytes(data if isinstance(data, bytes) else data.encode('utf-8'))
    temporary.replace(path)


def request(url, headers=None, attempts=3):
    parsed = urllib.parse.urlsplit(url)
    if parsed.scheme != 'https' or parsed.hostname != 'www.vatican.va':
        raise ValueError('Only the reviewed HTTPS Vatican source is allowed')
    for attempt in range(attempts):
        try:
            req = urllib.request.Request(url, headers={'User-Agent': AGENT, **(headers or {})})
            with urllib.request.urlopen(req, timeout=30) as response:
                final = urllib.parse.urlsplit(response.url)
                if final.scheme != 'https' or final.hostname != parsed.hostname:
                    raise ValueError('Unexpected source redirect')
                body = response.read(LIMIT + 1)
                if len(body) > LIMIT: raise ValueError('Source exceeds 2 MiB limit')
                return body, {key.lower(): value for key, value in response.headers.items()}
        except urllib.error.HTTPError as error:
            if error.code == 304: return None, {key.lower(): value for key, value in error.headers.items()}
            if error.code not in (429, 500, 502, 503, 504) or attempt == attempts - 1: raise
            retry_after = error.headers.get('Retry-After', '')
            delay = 2 ** attempt
            if retry_after.isdigit():
                delay = int(retry_after)
            elif retry_after:
                try:
                    when = parsedate_to_datetime(retry_after)
                    if when.tzinfo is None: when = when.replace(tzinfo=timezone.utc)
                    delay = max(0, when.timestamp() - time.time())
                except (ValueError, TypeError, OverflowError): pass
            # A long deferral is returned to the editor, never retried before the server permits.
            if delay > 10: raise
            time.sleep(delay)
        except (urllib.error.URLError, TimeoutError):
            if attempt == attempts - 1: raise
            time.sleep(2 ** attempt)
    raise RuntimeError('Unreachable retry state')


def fetch(offline=False):
    manifest = read_json(CONTENT / 'source-manifest.json')
    if manifest.get('version') != 1: raise ValueError('Unsupported source manifest version')
    for source in manifest['sources']:
        target = CONTENT / 'sources' / (source['id'] + '.json')
        if offline:
            snapshot = read_json(target)
            if digest(encoded(snapshot['paragraphs']).encode()) != snapshot['extraction_sha256']:
                raise ValueError('Source snapshot integrity mismatch')
            if snapshot['source_sha256'] != source['sha256'] or snapshot['url'] != source['url']:
                raise ValueError('Source snapshot does not match the manifest')
            print(f"Verified offline snapshot: {source['id']}"); continue
        cache = CONTENT / '.cache' / (source['id'] + '.html')
        meta_path = cache.with_suffix('.headers.json')
        meta = read_json(meta_path) if meta_path.exists() else {}
        robots_body, _ = request('https://www.vatican.va/robots.txt')
        robots = urllib.robotparser.RobotFileParser()
        robots.parse((robots_body or b'').decode('utf-8').splitlines())
        if not robots.can_fetch(AGENT, source['url']): raise ValueError('Source disallows this importer in robots.txt')
        headers = {}
        if cache.exists() and meta.get('sha256') == digest(cache.read_bytes()):
            if meta.get('etag'): headers['If-None-Match'] = meta['etag']
            if meta.get('modified'): headers['If-Modified-Since'] = meta['modified']
        body, response_headers = request(source['url'], headers)
        if body is None:
            if not cache.exists(): raise ValueError('304 without a cached body')
            body = cache.read_bytes()
        if source.get('sha256') and digest(body) != source['sha256']:
            raise ValueError('Upstream source changed. Review it and update the manifest digest before importing.')
        paragraphs = extract(body.decode('utf-8-sig'))
        snapshot = {'url': source['url'], 'source_sha256': digest(body),
                    'extraction_sha256': digest(encoded(paragraphs).encode()), 'paragraphs': paragraphs}
        atomic_write(target, encoded(snapshot))
        atomic_write(cache, body)
        atomic_write(meta_path, encoded({'etag': response_headers.get('etag'), 'modified': response_headers.get('last-modified'), 'sha256': digest(body)}))
        print(f"Imported {len(paragraphs)} paragraphs from {source['id']}; catalog unchanged")


def validate(catalog):
    if catalog.get('version') != 1: raise ValueError('Unsupported catalog version')
    prayers = catalog['prayers']; seen = set()
    for prayer in prayers:
        pid = prayer['id']
        if not re.fullmatch('[a-z0-9]+(?:-[a-z0-9]+)*', pid) or pid in seen: raise ValueError(f'Duplicate/invalid id: {pid}')
        seen.add(pid)
        for field in ('title', 'summary', 'portuguese', 'source', 'context'):
            if not prayer.get(field, '').strip(): raise ValueError(f'{pid}: missing {field}')
        if prayer['category'] not in CATEGORIES: raise ValueError(f'{pid}: invalid category')
        for url in prayer.get('sourceUrls', []):
            if urllib.parse.urlsplit(url).scheme != 'https': raise ValueError(f'{pid}: insecure source')
        verses = prayer.get('verses', [])
        if prayer.get('latin'):
            if not prayer.get('latinTitle') or not verses: raise ValueError(f'{pid}: Latin requires aligned verses')
            for field in ('portuguese', 'latin'):
                if normalized(' '.join(v[field] for v in verses)) != normalized(prayer[field]): raise ValueError(f'{pid}: {field} verses do not reconstruct text')
        for verse in verses:
            for gloss in verse.get('glosses', []):
                if gloss['latin'] not in verse['latin'] or not gloss['portuguese'].strip(): raise ValueError(f'{pid}: invalid gloss')
        for annotation in prayer.get('annotations', []):
            if annotation['term'] not in prayer['portuguese']: raise ValueError(f'{pid}: annotation not in text: {annotation["term"]}')
            for field in ('origin', 'today', 'scriptureReference', 'catechism'):
                if not annotation.get(field): raise ValueError(f'{pid}: incomplete explanation: {field}')
    missing = CATEGORIES - {p['category'] for p in prayers}
    if missing: raise ValueError(f'Empty categories: {missing}')
    return prayers


def kotlin_string(value):
    if value is None: return 'null'
    return json.dumps(value, ensure_ascii=False).replace('$', '\\$')


def generate(check=False):
    catalog = read_json(CONTENT / 'catalog.json'); prayers = validate(catalog)
    k = kotlin_string
    lines = ['// Generated by tools/treasury/treasury.py generate. Edit content/treasury/catalog.json.',
             'package com.sertumrosarum.domain.treasury', '', 'internal object GeneratedTreasury {',
             '    val prayers: List<TreasuryPrayer> by lazy { listOf(' + ', '.join(f'prayer{i}()' for i in range(len(prayers))) + ') }']
    for i, p in enumerate(prayers):
        lines += [f'    private fun prayer{i}() = TreasuryPrayer(', f'        id = {k(p["id"])}, title = {k(p["title"])}, category = PrayerCategory.{p["category"]},',
                  f'        summary = {k(p["summary"])}, portuguese = {k(p["portuguese"])},', f'        latinTitle = {k(p.get("latinTitle"))}, latin = {k(p.get("latin"))},', '        verses = listOf(']
        for v in p.get('verses', []):
            glosses = ', '.join(f'LatinGloss({k(g["latin"])}, {k(g["portuguese"])}, {k(g.get("note"))})' for g in v.get('glosses', []))
            lines += [f'            BilingualVerse({k(v["portuguese"])}, {k(v["latin"])}, listOf({glosses})),']
        lines += ['        ),', '        annotations = listOf(']
        for a in p.get('annotations', []):
            lines += ['            PrayerTermAnnotation(' + ', '.join(f'{f} = {k(a.get(f))}' for f in ('term','origin','today','scriptureQuote','scriptureReference','catechism')) + '),']
        lines += ['        ),', f'        source = {k(p["source"])}, context = {k(p["context"])},',
                  '        sourceUrls = listOf(' + ', '.join(k(u) for u in p.get('sourceUrls', [])) + '),',
                  '        keywords = listOf(' + ', '.join(k(t) for t in p.get('keywords', [])) + '),',
                  '        fixedPrayer = ' + ('com.sertumrosarum.domain.prayer.FixedPrayer.' + p['fixedPrayer'] if p.get('fixedPrayer') else 'null') + ',',
                  f'        latinSolo = {k(p.get("latinSolo"))},', '    )']
    lines += ['}', '']; result = '\n'.join(lines)
    if check:
        if not OUTPUT.exists() or OUTPUT.read_text() != result: raise ValueError('Generated Kotlin is stale: run generate')
    else: atomic_write(OUTPUT, result)
    print(f"{'Checked' if check else 'Generated'} {len(prayers)} prayers, {sum(bool(p.get('latin')) for p in prayers)} bilingual, {sum(len(p.get('annotations', [])) for p in prayers)} explanations")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest='command', required=True)
    f = sub.add_parser('fetch'); f.add_argument('--offline', action='store_true')
    g = sub.add_parser('generate'); g.add_argument('--check', action='store_true')
    args = parser.parse_args()
    try:
        if args.command == 'fetch': fetch(args.offline)
        else: generate(args.check)
    except (ValueError, OSError, KeyError) as error:
        parser.exit(1, f'Treasury: {error}\n')

if __name__ == '__main__': main()
