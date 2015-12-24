import sys
import os
import json
import geojson


def main():
    zone_file = os.path.abspath(sys.argv[1])
    out_path = os.path.splitext(zone_file)[0] + ".geojson"

    with open(zone_file) as f:
        data = json.load(f)
        zones = []

        for d in data:
            properties = {}
            properties['name'] = d['name']
            properties['displayName'] = d['displayName']
            properties['listOfCities'] = d['listOfCities']
            properties['zoneNumber'] = d['zoneNumber']
            properties['zoneNumberSince'] = d['zoneNumberSince']
            properties['nextZoneNumberAsOf'] = d['nextZoneNumberAsOf']
            properties['abroadLicensedVehicleZoneNumber'] = d['abroadLicensedVehicleZoneNumber']
            properties['abroadLicensedVehicleZoneNumberUntil'] = d['abroadLicensedVehicleZoneNumberUntil']
            properties['urlUmweltPlaketteDe'] = d['urlUmweltPlaketteDe']
            properties['urlBadgeOnline'] = d['urlBadgeOnline']
            properties['contactEmails'] = d['contactEmails']
            properties['geometrySource'] = d['geometrySource']
            properties['geometryUpdatedAt'] = d['geometryUpdatedAt']
            bbox_sw_lat = d['boundingBox']['southWest']['latitude']
            bbox_sw_lon = d['boundingBox']['southWest']['longitude']
            bbox_ne_lat = d['boundingBox']['northEast']['latitude']
            bbox_ne_lon = d['boundingBox']['northEast']['longitude']
            multi_point = geojson.MultiPoint([(bbox_sw_lon, bbox_sw_lat),
                                              (bbox_ne_lon, bbox_ne_lat)])
            feature = geojson.Feature(geometry=multi_point,
                                      properties=properties)
            zones.append(feature)

        feature_collection = geojson.FeatureCollection(zones)

        if geojson.is_valid(feature_collection):
            with open(out_path, 'w') as out_file:
                geojson.dump(feature_collection, out_file, indent=4)
                # print(geojson.dumps(geo_data, indent=4))


if __name__ == '__main__':
    main()
