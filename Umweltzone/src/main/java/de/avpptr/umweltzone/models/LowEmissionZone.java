/*
 *  Copyright (C) 2015  Tobias Preuss, Peter Vasil
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.avpptr.umweltzone.models;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

import de.avpptr.umweltzone.R;
import de.avpptr.umweltzone.Umweltzone;
import de.avpptr.umweltzone.analytics.TrackingPoint;
import de.avpptr.umweltzone.contract.LowEmissionZoneNumbers;
import de.avpptr.umweltzone.prefs.PreferencesHelper;
import de.avpptr.umweltzone.utils.BoundingBox;
import de.avpptr.umweltzone.utils.ContentProvider;

public class LowEmissionZone {

    public String name;

    public String displayName;

    public List<String> listOfCities;

    public BoundingBox boundingBox;

    @LowEmissionZoneNumbers.Color
    public int zoneNumber;

    public Date zoneNumberSince;

    public Date nextZoneNumberAsOf;

    public int abroadLicensedVehicleZoneNumber;

    public Date abroadLicensedVehicleZoneNumberUntil;

    public String urlUmweltPlaketteDe;

    public String urlBadgeOnline;

    public List<String> contactEmails;

    public String geometrySource;

    public Date geometryUpdatedAt;

    // Used for caching
    private static List<LowEmissionZone> mLowEmissionZones;

    @Nullable
    public static LowEmissionZone getRecentLowEmissionZone(Context context) {
        Umweltzone application = (Umweltzone) context.getApplicationContext();
        final PreferencesHelper preferencesHelper = application.getPreferencesHelper();
        String zoneName = preferencesHelper.restoreLastKnownLocationAsString();
        return getLowEmissionZone(context, zoneName);
    }

    @Nullable
    public static LowEmissionZone getDefaultLowEmissionZone(Context context) {
        String defaultZone = context.getString(R.string.config_default_zone_name);
        return getLowEmissionZone(context, defaultZone);
    }

    // TODO Parser should not be called more often then needed
    @Nullable
    private static LowEmissionZone getLowEmissionZone(Context context, String zoneName) {
        if (mLowEmissionZones == null) {
            mLowEmissionZones = ContentProvider.getLowEmissionZones(context);
        }
        if (mLowEmissionZones.isEmpty()) {
            Umweltzone.getTracker().trackError(TrackingPoint.ParsingZonesFromJSONFailedError, null);
            throw new IllegalStateException("Parsing zones from JSON failed.");
        }
        for (int i = 0, size = mLowEmissionZones.size(); i < size; i++) {
            LowEmissionZone lowEmissionZone = mLowEmissionZones.get(i);
            if (lowEmissionZone.name.equalsIgnoreCase(zoneName)) {
                return lowEmissionZone;
            }
        }
        return null;
    }

    public boolean containsGeometryInformation() {
        return geometrySource != null && geometryUpdatedAt != null;
    }

    @Override
    public String toString() {
        return "name: " + name +
                ", displayName: " + displayName +
                ", listOfCities: " + listOfCities +
                ", boundingBox: " + boundingBox +
                ", zoneNumber: " + zoneNumber +
                ", zoneNumberSince: " + zoneNumberSince +
                ", nextZoneNumberAsOf: " + nextZoneNumberAsOf +
                ", abroadLicensedVehicleZoneNumber: " + abroadLicensedVehicleZoneNumber +
                ", abroadLicensedVehicleZoneNumberUntil: " + abroadLicensedVehicleZoneNumberUntil +
                ", urlUmweltPlaketteDe: " + urlUmweltPlaketteDe +
                ", urlBadgeOnline: " + urlBadgeOnline +
                ", contactEmails: " + contactEmails +
                ", geometrySource: " + geometrySource +
                ", geometryUpdatedAt: " + geometryUpdatedAt;
    }

}
