# Fine art for the mysteries and lessons

Thirty-six works of sacred art accompany the twenty mysteries of the rosary and the sixteen catechesis lessons. Every work is in the public domain (all artists died well over 70 years ago), and every image comes from a museum that releases it under **CC0** in its own open-access programme: The Metropolitan Museum of Art, the National Gallery of Art (Washington), the Art Institute of Chicago and the Cleveland Museum of Art. No image comes from a collection with reproduction restrictions (for example Italian state museums).

The app credits each work under the painting, in its full-screen view (with a link to the museum page) and on the *Obras de arte e créditos* screen reachable from Catequese and from *Os vinte mistérios*. The registry lives in `app/src/main/java/com/sertumrosarum/ui/canvas/FineArt.kt`.

## Placement

- **Mysteries:** a framed painting under the title of each mystery's study sheet, and a thumbnail beside each mystery in *Os vinte mistérios*.
- **Lessons:** a framed painting after the teaching, just before its Scripture and Catechism sources.
- Paintings are shown whole (`ContentScale.Fit`) in a thin gold frame; thumbnails are centre crops. Touching a painting opens it large with its full credit.

## Processing

Images were downloaded from each museum's open-access API, resized to at most 1200 px on the long side and encoded as WebP (quality 80), about 5 MB in total. The Transfiguration is cropped from its album mount to the painted miniature; nothing else is cropped or retouched.

## Works

| Where | Work | Artist | Date | Collection | Source | Resource |
| --- | --- | --- | --- | --- | --- | --- |
| Mistério · Anunciação | A Anunciação | Masolino da Panicale | c. 1423–1424 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.18.html) | `art_mystery_joyful_1.webp` |
| Mistério · Visitação | A Visitação, com São Nicolau e Santo Antão | Piero di Cosimo | c. 1489–1490 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.505.html) | `art_mystery_joyful_2.webp` |
| Mistério · Nascimento de Jesus | A Natividade | Fra Bartolomeo | 1504–1507 | The Art Institute of Chicago | [museum page](https://www.artic.edu/artworks/184371) | `art_mystery_joyful_3.webp` |
| Mistério · Apresentação no Templo | A Apresentação no Templo | Álvaro Pires de Évora | c. 1405–1415 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437298) | `art_mystery_joyful_4.webp` |
| Mistério · Encontro no Templo | Cristo entre os doutores | Mestre dos Reis Católicos | c. 1495–1497 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.41656.html) | `art_mystery_joyful_5.webp` |
| Mistério · Batismo de Jesus | O Batismo de Cristo | Perugino | 1500–1505 | The Art Institute of Chicago | [museum page](https://www.artic.edu/artworks/16197) | `art_mystery_luminous_1.webp` |
| Mistério · Bodas de Caná | As Bodas de Caná | Juan de Flandes | c. 1497 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/436801) | `art_mystery_luminous_2.webp` |
| Mistério · Anúncio do Reino | O chamado de Pedro e André | Duccio di Buoninsegna | 1308–1311 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.282.html) | `art_mystery_luminous_3.webp` |
| Mistério · Transfiguração | A Transfiguração, de um evangeliário armênio | T'oros, o Diácono | 1311 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/751462) | `art_mystery_luminous_4.webp` |
| Mistério · Instituição da Eucaristia | A Última Ceia | Ugolino da Siena | c. 1325 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/459131) | `art_mystery_luminous_5.webp` |
| Mistério · Agonia no Horto | A Agonia no Horto | Rafael | c. 1504 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437371) | `art_mystery_sorrowful_1.webp` |
| Mistério · Flagelação | A Flagelação de Cristo | Bachiacca | c. 1512–1515 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.41696.html) | `art_mystery_sorrowful_2.webp` |
| Mistério · Coroação de espinhos | A Virgem dolorosa e Cristo coroado de espinhos | Oficina, segundo Dieric Bouts | c. 1520–1530 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/435763) | `art_mystery_sorrowful_3.webp` |
| Mistério · Jesus carrega a cruz | Cristo carregando a cruz | El Greco | c. 1577–1587 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/459087) | `art_mystery_sorrowful_4.webp` |
| Mistério · Crucificação | A Crucifixão | Fra Angelico | c. 1440 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/435577) | `art_mystery_sorrowful_5.webp` |
| Mistério · Ressurreição | A Ressurreição | Benvenuto di Giovanni | 1491 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.41670.html) | `art_mystery_glorious_1.webp` |
| Mistério · Ascensão | A Ascensão | Johann Koerbecke | 1456–1457 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.45890.html) | `art_mystery_glorious_2.webp` |
| Mistério · Pentecostes | Pentecostes | Oficina de Bernard van Orley | c. 1520 | The Art Institute of Chicago | [museum page](https://www.artic.edu/artworks/41217) | `art_mystery_glorious_3.webp` |
| Mistério · Assunção | A Assunção da Virgem | Nicolas Poussin | c. 1630–1632 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.46470.html) | `art_mystery_glorious_4.webp` |
| Mistério · Coroação de Maria | A Coroação da Virgem | Annibale Carracci | depois de 1595 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/435853) | `art_mystery_glorious_5.webp` |
| Lição · god-loves | Cristo acolhendo as crianças | Sébastien Bourdon | c. 1655 | The Art Institute of Chicago | [museum page](https://www.artic.edu/artworks/9672) | `art_lesson_god_loves.webp` |
| Lição · trinity | A Trindade | Laurent Girardin | c. 1460 | The Cleveland Museum of Art | [museum page](https://clevelandart.org/art/1960.79) | `art_lesson_trinity.webp` |
| Lição · jesus | Salvator Mundi | Hans Memling | 1480–1485 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437061) | `art_lesson_jesus.webp` |
| Lição · church-mary | A Virgem com o Menino e santos no jardim fechado | Seguidor de Robert Campin | c. 1440–1460 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.45888.html) | `art_lesson_church_mary.webp` |
| Lição · sacraments | O Batismo de Cristo | Nicolas Poussin | 1641–1642 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.32692.html) | `art_lesson_sacraments.webp` |
| Lição · mass | A Missa de São Gregório | Pintor espanhol | c. 1490–1500 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437722) | `art_lesson_mass.webp` |
| Lição · eucharist | A Ceia em Emaús | Diego Velázquez | 1622–1623 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437871) | `art_lesson_eucharist.webp` |
| Lição · reconciliation | O regresso do filho pródigo | Bartolomé Esteban Murillo | 1667–1670 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.34956.html) | `art_lesson_reconciliation.webp` |
| Lição · dignity | Vestir os nus | Michiel Sweerts | c. 1661 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437769) | `art_lesson_dignity.webp` |
| Lição · grace-freedom | As lágrimas de São Pedro | Jusepe de Ribera | c. 1612–1613 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/441971) | `art_lesson_grace_freedom.webp` |
| Lição · mercy | O Bom Samaritano | Domenico Fetti | c. 1618–1622 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/436302) | `art_lesson_mercy.webp` |
| Lição · forgiveness | Cristo e a mulher adúltera | Lucas Cranach, o Jovem, e oficina | c. 1545–1550 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/436034) | `art_lesson_forgiveness.webp` |
| Lição · prayer | Madalena penitente | Georges de La Tour | c. 1640 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/436839) | `art_lesson_prayer.webp` |
| Lição · rosary | Os quinze mistérios e a Virgem do Rosário | Pintor de Bruxelas | c. 1515–1520 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/437216) | `art_lesson_rosary.webp` |
| Lição · year | A Natividade com os profetas Isaías e Ezequiel | Duccio di Buoninsegna | 1308–1311 | National Gallery of Art, Washington | [museum page](https://www.nga.gov/collection/art-object-page.10.html) | `art_lesson_year.webp` |
| Lição · daily | Entre os humildes | Léon-Augustin Lhermitte | 1905 | The Metropolitan Museum of Art | [museum page](https://www.metmuseum.org/art/collection/search/436883) | `art_lesson_daily.webp` |

Museum titles in English, for reference: The Annunciation; The Visitation with Saint Nicholas and Saint Anthony Abbot; The Nativity; The Presentation in the Temple; Christ among the Doctors; The Baptism of Christ; The Marriage Feast at Cana; The Calling of the Apostles Peter and Andrew; Four miniatures of the life of Christ from a gospel book of 1311 (Presentation, Baptism, Transfiguration, Entry into Jerusalem); The Last Supper; The Agony in the Garden; The Flagellation of Christ; The Mourning Virgin; Christ Crowned with Thorns; Christ Carrying the Cross; The Crucifixion; The Resurrection; The Ascension; Pentecost; The Assumption of the Virgin; The Coronation of the Virgin; Christ Receiving the Children; The Trinity; Salvator Mundi; Madonna and Child with Saints in the Enclosed Garden; The Baptism of Christ; The Mass of Saint Gregory; The Supper at Emmaus; The Return of the Prodigal Son; Clothing the Naked; The Tears of Saint Peter; The Good Samaritan; Christ and the Adulteress; The Penitent Magdalen; The Fifteen Mysteries and the Virgin of the Rosary; The Nativity with the Prophets Isaiah and Ezekiel; Among the Humble.
