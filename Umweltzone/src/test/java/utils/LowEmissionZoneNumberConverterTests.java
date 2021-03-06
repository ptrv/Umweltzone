/*
 *  Copyright (C) 2015  Tobias Preuss
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

package utils;

import org.junit.Test;

import de.avpptr.umweltzone.R;
import de.avpptr.umweltzone.contract.LowEmissionZoneNumbers;
import de.avpptr.umweltzone.contract.Resources;
import de.avpptr.umweltzone.utils.LowEmissionZoneNumberConverter;

import static org.assertj.core.api.Assertions.assertThat;

public class LowEmissionZoneNumberConverterTests {

    public final static
    @LowEmissionZoneNumbers.Color
    int INVALID_COLOR = 1;

    @Test
    public void testGetStatusDrawable() {
        assertThat(LowEmissionZoneNumberConverter.getStatusDrawable(INVALID_COLOR))
                .isEqualTo(Resources.INVALID_RESOURCE_ID);
        assertThat(LowEmissionZoneNumberConverter.getStatusDrawable(LowEmissionZoneNumbers.RED))
                .isEqualTo(R.drawable.umweltzone_status_2);
        assertThat(LowEmissionZoneNumberConverter.getStatusDrawable(LowEmissionZoneNumbers.YELLOW))
                .isEqualTo(R.drawable.umweltzone_status_3);
        assertThat(LowEmissionZoneNumberConverter.getStatusDrawable(LowEmissionZoneNumbers.GREEN))
                .isEqualTo(R.drawable.umweltzone_status_4);
    }

    @Test
    public void testGetColor() {
        assertThat(LowEmissionZoneNumberConverter.getColor(INVALID_COLOR))
                .isEqualTo(R.color.city_zone_none);
        assertThat(LowEmissionZoneNumberConverter.getColor(LowEmissionZoneNumbers.RED))
                .isEqualTo(R.color.city_zone_2);
        assertThat(LowEmissionZoneNumberConverter.getColor(LowEmissionZoneNumbers.YELLOW))
                .isEqualTo(R.color.city_zone_3);
        assertThat(LowEmissionZoneNumberConverter.getColor(LowEmissionZoneNumbers.GREEN))
                .isEqualTo(R.color.city_zone_4);
    }

    @Test
    public void testGetColorString() {
        assertThat(LowEmissionZoneNumberConverter.getColorString(INVALID_COLOR))
                .isEqualTo(Resources.INVALID_RESOURCE_ID);
        assertThat(LowEmissionZoneNumberConverter.getColorString(LowEmissionZoneNumbers.RED))
                .isEqualTo(R.string.city_info_zone_number_since_text_fragment_red);
        assertThat(LowEmissionZoneNumberConverter.getColorString(LowEmissionZoneNumbers.YELLOW))
                .isEqualTo(R.string.city_info_zone_number_since_text_fragment_yellow);
        assertThat(LowEmissionZoneNumberConverter.getColorString(LowEmissionZoneNumbers.GREEN))
                .isEqualTo(R.string.city_info_zone_number_since_text_fragment_green);
    }

}
