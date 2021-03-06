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

package de.avpptr.umweltzone.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ShareCompat;

import de.avpptr.umweltzone.about.AboutActivity;
import de.avpptr.umweltzone.cities.CitiesActivity;
import de.avpptr.umweltzone.city.CityInfoActivity;
import de.avpptr.umweltzone.contract.BundleKeys;
import de.avpptr.umweltzone.faqs.FaqActivity;
import de.avpptr.umweltzone.feedback.FeedbackActivity;
import de.avpptr.umweltzone.map.MainActivity;

public class IntentHelper {

    public static Intent getSendEmailIntent(
            Activity activity,
            String[] toRecipients,
            String[] bccRecipient,
            String subject,
            String message) {
        return ShareCompat.IntentBuilder.from(activity)
                .setEmailTo(toRecipients)
                .setEmailBcc(bccRecipient)
                .setSubject(subject)
                .setType("message/rfc822")
                .setHtmlText(message)
                .getIntent();
    }

    public static Intent getNewMapIntent(Activity activity) {
        final Intent intent = getIntent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getHomeIntent(Activity activity) {
        final Intent intent = getIntent(activity, MainActivity.class);
        intent.putExtra(BundleKeys.HOME, BundleKeys.HOME);
        return intent;
    }

    public static Intent getCityInfoIntent(Activity activity) {
        return getIntent(activity, CityInfoActivity.class);
    }

    public static Intent getFeedbackIntent(Activity activity) {
        return getIntent(activity, FeedbackActivity.class);
    }

    public static Intent getAboutIntent(Activity activity) {
        return getIntent(activity, AboutActivity.class);
    }

    public static Intent getCitiesIntent(Activity activity) {
        return getIntent(activity, CitiesActivity.class);
    }

    public static Intent getFaqsIntent(Activity activity) {
        return getIntent(activity, FaqActivity.class);
    }

    private static Intent getIntent(Activity activity, Class<?> clazz) {
        final Intent intent = new Intent(activity, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

}
