import copy
import importlib.util
import json
import tempfile
import unittest
import urllib.error
from pathlib import Path
from unittest.mock import patch

MODULE = Path(__file__).resolve().parents[1] / 'treasury.py'
spec = importlib.util.spec_from_file_location('treasury', MODULE)
t = importlib.util.module_from_spec(spec); spec.loader.exec_module(t)


class ImporterTest(unittest.TestCase):
    def setUp(self): self.catalog = t.read_json(t.CONTENT / 'catalog.json')

    def test_full_catalog_compiles_and_checked_output_is_current(self):
        t.generate(check=True)
        self.assertGreaterEqual(len(t.validate(self.catalog)), 60)

    def test_rejects_duplicate_ids_missing_context_and_unknown_category(self):
        mutations = [lambda c: c['prayers'].append(c['prayers'][0]),
                     lambda c: c['prayers'][0].pop('context'),
                     lambda c: c['prayers'][0].update(category='UNKNOWN')]
        for mutate in mutations:
            c = copy.deepcopy(self.catalog); mutate(c)
            with self.assertRaises(ValueError): t.validate(c)

    def test_rejects_drift_in_either_side_of_the_alignment(self):
        for field in ('portuguese', 'latin'):
            c = copy.deepcopy(self.catalog); c['prayers'][0]['verses'][0][field] += ' drift'
            with self.assertRaisesRegex(ValueError, 'reconstruct'): t.validate(c)

    def test_rejects_dangling_annotations_glosses_and_insecure_sources(self):
        for field, value in [('term', 'not in the prayer'), ('origin', '')]:
            c = copy.deepcopy(self.catalog); c['prayers'][0]['annotations'][0][field] = value
            with self.assertRaises(ValueError): t.validate(c)
        c = copy.deepcopy(self.catalog); c['prayers'][0]['verses'][0]['glosses'][0]['latin'] = 'absent'
        with self.assertRaisesRegex(ValueError, 'gloss'): t.validate(c)
        c = copy.deepcopy(self.catalog); c['prayers'][0]['sourceUrls'] = ['http://example.com']
        with self.assertRaisesRegex(ValueError, 'insecure'): t.validate(c)

    def test_kotlin_literals_escape_dollar_quotes_newline_and_backslash(self):
        self.assertEqual('"\\$name \\"x\\"\\n\\\\"', t.kotlin_string('$name "x"\n\\'))

    def test_parser_decodes_entities_and_keeps_verses_but_ignores_scripts(self):
        p = t.Paragraphs(); p.feed('<p><b>Ave</b><br>gra&ccedil;a<script>bad()</script><br>plena</p>'); p.flush()
        self.assertEqual([{'heading': 'Ave', 'text': 'Ave\ngraça\nplena'}], p.rows)

    def test_layout_drift_fails_instead_of_silently_importing_unrelated_text(self):
        with self.assertRaisesRegex(ValueError, 'marker missing'): t.extract('<p>Other document</p>')
        with self.assertRaisesRegex(ValueError, 'end marker'): t.extract('<p>A) ORAÇÕES COMUNS</p>')

    def test_offline_snapshot_integrity_and_manifest_identity(self):
        t.fetch(offline=True)
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); (root / 'sources').mkdir()
            (root / 'source-manifest.json').write_text((t.CONTENT / 'source-manifest.json').read_text())
            snapshot = t.read_json(t.CONTENT / 'sources/compendium.json')
            snapshot['paragraphs'][0]['text'] += ' tampered'
            (root / 'sources/compendium.json').write_text(t.encoded(snapshot))
            with patch.object(t, 'CONTENT', root), self.assertRaisesRegex(ValueError, 'integrity'):
                t.fetch(offline=True)

    def test_network_is_limited_to_the_reviewed_https_host(self):
        for url in ('http://www.vatican.va/x', 'https://evil.example/x', 'file:///etc/passwd'):
            with self.assertRaisesRegex(ValueError, 'reviewed HTTPS'): t.request(url)

    def test_retries_transient_errors_but_not_not_found(self):
        error = urllib.error.HTTPError('https://www.vatican.va/x', 503, 'busy', {}, None)
        with patch.object(t.urllib.request, 'urlopen', side_effect=error) as opening, patch.object(t.time, 'sleep'):
            with self.assertRaises(urllib.error.HTTPError): t.request(error.url)
            self.assertEqual(3, opening.call_count)
        error = urllib.error.HTTPError('https://www.vatican.va/x', 404, 'missing', {}, None)
        with patch.object(t.urllib.request, 'urlopen', side_effect=error) as opening:
            with self.assertRaises(urllib.error.HTTPError): t.request(error.url)
            self.assertEqual(1, opening.call_count)

    def test_long_retry_after_is_not_shortened_or_retried_early(self):
        for value in ('120', 'Thu, 01 Jan 1970 00:01:00 GMT'):
            error = urllib.error.HTTPError('https://www.vatican.va/x', 429, 'busy', {'Retry-After': value}, None)
            with patch.object(t.urllib.request, 'urlopen', side_effect=error) as opening, patch.object(t.time, 'sleep') as sleep, patch.object(t.time, 'time', return_value=0):
                with self.assertRaises(urllib.error.HTTPError): t.request(error.url)
                self.assertEqual(1, opening.call_count)
                sleep.assert_not_called()

    def test_changed_source_does_not_overwrite_snapshot_or_catalog(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); (root / 'source-manifest.json').write_text((t.CONTENT / 'source-manifest.json').read_text())
            root.joinpath('catalog.json').write_text('editorial work')
            def response(url, headers=None):
                return (b'User-agent: *\nAllow: /' if url.endswith('robots.txt') else b'changed source', {})
            with patch.object(t, 'CONTENT', root), patch.object(t, 'request', side_effect=response):
                with self.assertRaisesRegex(ValueError, 'source changed'): t.fetch()
            self.assertEqual('editorial work', root.joinpath('catalog.json').read_text())
            self.assertFalse(root.joinpath('sources/compendium.json').exists())

    def test_verified_cache_uses_conditional_request_and_reuses_304_body(self):
        from html import escape
        snapshot = t.read_json(t.CONTENT / 'sources/compendium.json')
        paragraphs = ['<p>A) ORAÇÕES COMUNS</p>']
        for row in snapshot['paragraphs']:
            heading, text = row['heading'], row['text']
            body = text[len(heading):] if heading and text.startswith(heading) else text
            paragraphs.append('<p><b>' + escape(heading) + '</b>' + escape(body).replace('\n', '<br>') + '</p>')
        paragraphs.append('<p>B) FÓRMULAS DE DOUTRINA CATÓLICA</p>')
        html = ''.join(paragraphs).encode()
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp); (root / '.cache').mkdir()
            url = snapshot['url']
            (root / 'source-manifest.json').write_text(t.encoded({'version': 1, 'sources': [{'id': 'compendium', 'url': url, 'sha256': t.digest(html)}]}))
            (root / '.cache/compendium.html').write_bytes(html)
            (root / '.cache/compendium.headers.json').write_text(t.encoded({'sha256': t.digest(html), 'etag': 'version-1'}))
            def response(url, headers=None):
                if url.endswith('robots.txt'): return b'User-agent: *\nAllow: /', {}
                self.assertEqual({'If-None-Match': 'version-1'}, headers)
                return None, {}
            with patch.object(t, 'CONTENT', root), patch.object(t, 'request', side_effect=response): t.fetch()
            self.assertEqual(t.digest(html), t.read_json(root / 'sources/compendium.json')['source_sha256'])

    def test_response_size_limit_is_enforced_before_extraction(self):
        class Response:
            url = 'https://www.vatican.va/x'
            headers = {}
            def __enter__(self): return self
            def __exit__(self, *args): pass
            def read(self, limit): return b'x' * limit
        with patch.object(t.urllib.request, 'urlopen', return_value=Response()):
            with self.assertRaisesRegex(ValueError, '2 MiB'): t.request(Response.url)

    def test_stale_generated_kotlin_fails_check(self):
        with tempfile.TemporaryDirectory() as tmp, patch.object(t, 'OUTPUT', Path(tmp) / 'Generated.kt'):
            with self.assertRaisesRegex(ValueError, 'stale'): t.generate(check=True)
            t.generate(); original = t.OUTPUT.read_bytes(); t.generate()
            self.assertEqual(original, t.OUTPUT.read_bytes())


if __name__ == '__main__': unittest.main()
