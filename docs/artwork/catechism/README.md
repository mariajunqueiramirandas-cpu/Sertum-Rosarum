# Illustrations that teach

These four original model-generated watercolor and gouache illustrations support the expanded [catechism curriculum](../../catechism-curriculum.md). They extend Sertum's ivory, sage, Marian blue and terracotta palette into scenes chosen for a particular teaching task.

| Image | Teaching task | Runtime file |
| --- | --- | --- |
| [Emmaus](masters/emmaus.png) | Notice Christ's initiative, the disciples' listening and the place of Scripture | `catechism_emmaus.webp` |
| [Baptism](masters/baptism.png) | Identify water, white garment, candle and the faith of the community | `catechism_baptism.webp` |
| [Mercy](masters/mercy.png) | Distinguish and connect bodily care, listening and companionship | `catechism_mercy.webp` |
| [Daily prayer](masters/daily.png) | Relate reading, service and reflection across an ordinary day | `catechism_daily.webp` |

Every image appears beside an observation question and a revealable explanation. Each is identified as generated with AI, has a descriptive accessibility label and opens larger with a written description and a close action. The baptism illustration is not a complete ritual guide. Emmaus is an imaginative representation of Luke 24, not historical documentation. The contemporary scenes suggest possible actions rather than an obligatory lifestyle. Abstract doctrines use separate native-text concept maps with their interpretive limits stated explicitly.

The PNG masters are 1536 × 1024. The app uses 960 × 640 WebPs, proportional exports with no crop, so hands, faces and contextual signs remain visible. They occupy about 0.75 MiB compressed, with approximately 2.34 MiB RGBA memory for the image in the open lesson. The bitmap is remembered for the lesson and shared with its viewer. No image is downloaded at runtime. [prompts.json](prompts.json) records provenance, prompts, hashes and export dimensions.

```sh
for subject in emmaus baptism mercy daily; do
  magick "docs/artwork/catechism/masters/$subject.png" \
    -resize 960x640 -define webp:method=6 -quality 88 \
    "app/src/main/res/drawable-nodpi/catechism_$subject.webp"
done
```
