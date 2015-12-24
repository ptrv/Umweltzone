/*
 *  Copyright (C) 2014  Tobias Preuss, Peter Vasil
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

package de.avpptr.umweltzone.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.v4.util.LruCache;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONException;
import org.ligi.tracedroid.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.avpptr.umweltzone.R;
import de.avpptr.umweltzone.Umweltzone;
import de.avpptr.umweltzone.analytics.TrackingPoint;
import de.avpptr.umweltzone.models.Circuit;
import de.avpptr.umweltzone.models.CircuitDeserializer;
import de.avpptr.umweltzone.models.Faq;
import de.avpptr.umweltzone.models.LowEmissionZone;

public abstract class ContentProvider {


    private static final LruCache<String, List<Circuit>> CIRCUITS_CACHE
            = new LruCache<String, List<Circuit>>(6);

    private static final LruCache<String, Integer> RESOURCE_ID_CACHE
            = new LruCache<String, Integer>(6);

    public static void enforceContentUpdate() {
        // Clear caches
        CIRCUITS_CACHE.evictAll();
        RESOURCE_ID_CACHE.evictAll();
    }

    @NonNull
    public static List<Faq> getFaqs(final Context context) {
        // Do not accidentally compare with Locale.GERMAN
        if (Locale.getDefault().equals(Locale.GERMANY)) {
            return getContent(context, "faqs_de", Faq.class);
        }
        return getContent(context, "faqs_en", Faq.class);
    }

    @NonNull
    public static List<LowEmissionZone> getLowEmissionZones(final Context context) {
        return getGeoJsonContent(context, "zones_de", LowEmissionZone.class);
    }

    @NonNull
    public static List<Circuit> getCircuits(final Context context, final String zoneName) {
        String keyForZone = generateKeyForZoneWith(zoneName);
        List<Circuit> circuits = CIRCUITS_CACHE.get(keyForZone);
        if (circuits == null) {
            circuits = getContent(context, keyForZone, Circuit.class);
            CIRCUITS_CACHE.put(keyForZone, circuits);
        }
        return circuits;
    }

    private static <T> List<T> getGeoJsonContent(Context context, String fileName, Class<T> contentType) {
        int rawResourceId = getResourceId(context, fileName, "raw");
        InputStream inputStream = context.getResources().openRawResource(rawResourceId);

        try {
            GeoJSONObject geoJSON = GeoJSON.parse(inputStream);
            if (LowEmissionZone.class == contentType) {
                List<LowEmissionZone> lezList = new ArrayList<>();
                if (geoJSON.getType() == GeoJSON.TYPE_FEATURE_COLLECTION) {
                    FeatureCollection featureCollection = (FeatureCollection) geoJSON;
                    for (Feature feature : featureCollection.getFeatures()) {
                        LowEmissionZone lez = LowEmissionZone.fromGeoJsonFeature(context, feature);
                        lezList.add(lez);
                    }
                }
                return (List<T>) lezList;
            } else if (Circuit.class == contentType) {
                List<Circuit> circuits = new ArrayList<>();

                return (List<T>) circuits;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    private static String generateKeyForZoneWith(String zoneName) {
        return "zone_" + zoneName;
    }

    @NonNull
    private static <T> List<T> getContent(
            final Context context,
            final String fileName,
            Class<T> contentType) {
        return getContent(context, fileName, "raw", contentType);
    }

    @SuppressWarnings("unchecked") // for Collections.EMPTY_LIST
    @NonNull
    private static <T> List<T> getContent(
            final Context context,
            final String fileName,
            final String folderName,
            Class<T> contentType) {
        int rawResourceId = getResourceId(context, fileName, folderName);

        InputStream inputStream = context.getResources().openRawResource(rawResourceId);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Circuit.class, new CircuitDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        String datePattern = context.getString(R.string.config_zone_number_since_date_format);
        objectMapper.setDateFormat(new SimpleDateFormat(datePattern, Locale.getDefault()));
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, contentType);
            return objectMapper.readValue(inputStream, collectionType);
        } catch (IOException e) {
            // TODO Aware that app will crash when JSON is mis-structured.
            e.printStackTrace();
        }
        Log.e(ContentProvider.class.getName(), "Failure parsing zone data for: " + fileName);
        return Collections.EMPTY_LIST;
    }

    @RawRes
    static Integer getResourceId(Context context, String fileName, String folderName) {
        String resourceKey = getFilePath(folderName, fileName);
        Integer rawResourceId = RESOURCE_ID_CACHE.get(resourceKey);
        if (rawResourceId == null) {
            // Invoke cache
            rawResourceId = getRawResourceId(context, fileName, folderName);
            RESOURCE_ID_CACHE.put(resourceKey, rawResourceId);
        }
        return rawResourceId;
    }

    @RawRes
    private static int getRawResourceId(Context context, String fileName, String folderName) {
        final Resources resources = context.getResources();
        // Look-up identifier using reflection (expensive)
        int rawResourceId = resources.getIdentifier(fileName, folderName, context.getPackageName());
        if (rawResourceId == de.avpptr.umweltzone.contract.Resources.INVALID_RESOURCE_ID) {
            String filePath = getFilePath(folderName, fileName);
            Umweltzone.getTracker().trackError(
                    TrackingPoint.ResourceNotFoundError,
                    filePath);
            throw new IllegalStateException(
                    "Resource for file path '" + filePath + "' not found.");
        }
        return rawResourceId;
    }

    private static String getFilePath(final String folderName, final String fileName) {
        return folderName + "/" + fileName;
    }

}
