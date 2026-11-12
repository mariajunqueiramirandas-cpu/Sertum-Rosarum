# Bundled fonts

Sertum uses fixed instances of Fraunces and Nunito. The original variable fonts are preserved in [sources](sources), with their SIL Open Font Licenses beside this file. Generated instances use the internal family names Sertum Display, Sertum Prayer, and Sertum Controls; copyright and license records are retained.

The source Nunito font defaults to weight 200, and Fraunces defaults to weight 900. Devices that ignore runtime variation settings can therefore show extremely thin supporting text and very heavy headings, even when Compose requests regular or bold weights. Separate static files remove that dependency on device variation handling and font-cache behavior.

| Family | Fixed instances |
| --- | --- |
| Fraunces display | 400 and 500; optical size 48, softness 100, wonk 0 |
| Fraunces prayer | 400; optical size 30, softness 100, wonk 0 |
| Nunito controls | 400, 500, 600, 700 |

To reproduce the app resources from the preserved sources:

```bash
python3 -m pip install -r docs/fonts/requirements.txt
python3 docs/fonts/build_fonts.py
```

FontTools is only needed for regeneration. The app and normal builds load the committed TTF resources directly and require no font generation step or network connection.
