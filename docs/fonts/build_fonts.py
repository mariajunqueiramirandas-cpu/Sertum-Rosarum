#!/usr/bin/env python3
"""Build fixed font instances so Android never falls back to variable defaults."""

from pathlib import Path

from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont

ROOT = Path(__file__).resolve().parents[2]
SOURCES = Path(__file__).resolve().parent / "sources"
OUTPUT = ROOT / "app/src/main/res/font"


def export(source: str, filename: str, family: str, style: str, weight: int, **axes: float) -> None:
    font = TTFont(SOURCES / source, recalcTimestamp=False)
    instance = instantiateVariableFont(font, {"wght": weight, **axes}, inplace=True)
    instance.recalcTimestamp = False
    # Distinct names also prevent OEM font caches from confusing the instances.
    postscript = family.replace(" ", "") + "-" + style.replace(" ", "")
    names = {1: family, 2: style, 3: postscript, 4: family + " " + style, 6: postscript, 16: family, 17: style}
    for record in list(instance["name"].names):
        if record.nameID in names:
            instance["name"].setName(names[record.nameID], record.nameID, record.platformID, record.platEncID, record.langID)
    for name_id, value in names.items():
        instance["name"].setName(value, name_id, 3, 1, 0x409)
    instance["OS/2"].usWeightClass = weight
    instance["OS/2"].fsSelection &= ~((1 << 5) | (1 << 6))
    instance["OS/2"].fsSelection |= 1 << (5 if weight >= 700 else 6)
    instance["head"].macStyle = (instance["head"].macStyle & ~1) | (1 if weight >= 700 else 0)
    assert "fvar" not in instance, "All variation axes must be fixed"
    OUTPUT.mkdir(parents=True, exist_ok=True)
    instance.save(OUTPUT / filename)
    print(f"{filename}: {weight}, {dict(axes)}")


if __name__ == "__main__":
    export("fraunces-variable.ttf", "fraunces_display_regular.ttf", "Sertum Display", "Regular", 400, opsz=48, SOFT=100, WONK=0)
    export("fraunces-variable.ttf", "fraunces_display_medium.ttf", "Sertum Display", "Medium", 500, opsz=48, SOFT=100, WONK=0)
    export("fraunces-variable.ttf", "fraunces_text_regular.ttf", "Sertum Prayer", "Regular", 400, opsz=30, SOFT=100, WONK=0)
    for style, weight in [("Regular", 400), ("Medium", 500), ("Semibold", 600), ("Bold", 700)]:
        export("nunito-variable.ttf", f"nunito_{style.lower()}.ttf", "Sertum Controls", style, weight)
