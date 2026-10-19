# Ramen tonkotsu con chashu y ajitama — publication record

- Date: 2026-10-19
- Destination: production, https://humami.es/meals/6ad686dc306ea1a7e37345cd
- Operation: `POST /api/meals` (HTTP 201, id `6ad686dc306ea1a7e37345cd`), then `PUT /api/meals/6ad686dc306ea1a7e37345cd/image` (HTTP 200), with `scripts/humami-api.sh`.
- Payload: [2026-10-19-ramen-tonkotsu-payload.json](2026-10-19-ramen-tonkotsu-payload.json), validated with `scripts/validate-mealrequest.py`.
- Source and credit:
  - The owner's own version, with the tare taken from the chashu braise and eggs marinated only in tare.
  - Inspired by Diego Doal's video "Ramen Tonkotsu casero desde cero" (https://www.youtube.com/watch?v=5phw7zyrnmQ). It is credited in the description and rewritten in Humami's own words, with no text or images reused.
- Image: the owner's own photo (JPEG, 1839×2000, sha256 `611370e97507ca19ff3b0fe8ea93ba2ecd106eaa0ada5b6fc6e6ea8f1dba9396`), uploaded unedited. The backend normalized it to a 1600×900 center crop at `https://img.humami.es/Ramen_tonkotsu_con_chashu_y_ajitama_5557f3d9-1fd6-4f37-b0b4-675685ef5b63.jpg`.
- Verified:
  - API returns 4 preparations and 9 FAQs.
  - The meal page returns 200 with the CDN `og:image`.
  - Search for "ramen" ranks it first.
