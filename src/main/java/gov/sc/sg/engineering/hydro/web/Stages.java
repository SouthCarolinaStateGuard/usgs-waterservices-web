/*
 * Copyright 2018 South Carolina State Guard.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.sc.sg.engineering.hydro.web;

import lombok.Data;
import lombok.NonNull;
import org.primefaces.json.JSONObject;

/**
 *
 * @author John Yeary
 */
@Data
public final class Stages {

    private final String siteCode;
    private final int actionLevel;
    private final int floodLevel;
    private final int moderateFloodLevel;
    private final int majorFloodLevel;

    public Stages(final String siteCode, final int actionLevel, final int floodLevel, final int moderateFloodLevel,
            final int majorFloodLevel) {
        this.siteCode = siteCode;
        this.actionLevel = actionLevel;
        this.floodLevel = floodLevel;
        this.moderateFloodLevel = moderateFloodLevel;
        this.majorFloodLevel = majorFloodLevel;
    }

    /**
     * Converts the current {@code Stages} object into a JSON object.
     *
     * @return A JSON object representing this class.
     */
    public String toJson() {
        return new JSONObject(this).toString();
    }

    /**
     * Convert a {@link JSONObject} into a {@code Stages} class.
     *
     * @param jsonObject The {@link JSONObject} to convert to a class.
     * @return A {@code Stages} object from the JSON provided, or an empty object with all values set to zero if it
     * could not be converted.
     */
    public static Stages toObject(@NonNull final JSONObject jsonObject) {
        return new Stages(jsonObject.optString("siteCode", ""), jsonObject.optInt("actionLevel", 0),
                jsonObject.optInt("floodLevel", 0), jsonObject.optInt("moderateFloodLevel", 0),
                jsonObject.optInt("majorFloodLevel", 0));
    }

    /**
     * Convert a JSON string into a {@code Stages} class.
     *
     * @param json The JSON string to convert.
     * @return A {@code Stages} object from the JSON provided, or an empty object with all values set to zero if it
     * could not be converted.
     */
    public static Stages toObject(@NonNull final String json) {
        return toObject(new JSONObject(json));
    }

}
