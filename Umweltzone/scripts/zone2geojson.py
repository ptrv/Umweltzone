import sys
import os
import json
import geojson


def main():
    zone_file = os.path.abspath(sys.argv[1])
    out_path = os.path.splitext(zone_file)[0] + ".geojson"

    with open(zone_file) as f:
        data = json.load(f)
        points = []
        for d in data[0]:
            lat = d['latitude']
            lon = d['longitude']
            points.append((lon, lat))

        geo_data = geojson.Polygon([points])

        if geojson.is_valid(geo_data):
            with open(out_path, 'w') as out_file:
                geojson.dump(geo_data, out_file, indent=4)
                # print(geojson.dumps(geo_data, indent=4))


if __name__ == '__main__':
    main()
