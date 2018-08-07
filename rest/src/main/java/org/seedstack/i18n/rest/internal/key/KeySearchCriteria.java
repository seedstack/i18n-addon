/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;

import org.seedstack.i18n.rest.internal.shared.BooleanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class KeySearchCriteria {

    public static final String IS_APPROX = "isApprox";
    public static final String IS_MISSING = "isMissing";
    public static final String IS_OUTDATED = "isOutdated";
    public static final String SEARCH_NAME = "searchName";
    public static final String LOCALE = "locale";

    private Boolean isApprox;
    private Boolean isMissing;
    private Boolean isOutdated;
    private String locale;
    private String name;

    private KeySearchCriteria() {
    }

    public KeySearchCriteria(Boolean isMissing, Boolean isApprox, Boolean isOutdated, String searchName) {
        this.setMissing(BooleanUtils.falseToNull(isMissing));
        this.setApprox(BooleanUtils.falseToNull(isApprox));
        this.setOutdated(BooleanUtils.falseToNull(isOutdated));
        this.setName(searchName);
    }

    public KeySearchCriteria(Boolean isMissing, Boolean isApprox, Boolean isOutdated, String searchName, String locale) {
        this.setMissing(BooleanUtils.falseToNull(isMissing));
        this.setApprox(BooleanUtils.falseToNull(isApprox));
        this.setOutdated(BooleanUtils.falseToNull(isOutdated));
        this.setName(searchName);
        this.setLocale(locale);
    }

    public static KeySearchCriteria fromMap(Map<String, Object> criteria) {
        KeySearchCriteria keySearchCriteria = new KeySearchCriteria();
        keySearchCriteria.setApprox((Boolean) criteria.get(IS_APPROX));
        keySearchCriteria.setMissing((Boolean) criteria.get(IS_MISSING));
        keySearchCriteria.setOutdated((Boolean) criteria.get(IS_OUTDATED));
        keySearchCriteria.setName((String) criteria.get(SEARCH_NAME));
        keySearchCriteria.setLocale((String) criteria.get(LOCALE));
        return keySearchCriteria;
    }

    public Boolean getApprox() {
        return isApprox;
    }

    void setApprox(Boolean approx) {
        isApprox = approx;
    }

    public Boolean getMissing() {
        return isMissing;
    }

    void setMissing(Boolean missing) {
        isMissing = missing;
    }

    public Boolean getOutdated() {
        return isOutdated;
    }

    void setOutdated(Boolean outdated) {
        isOutdated = outdated;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(IS_APPROX, isApprox);
        map.put(IS_MISSING, isMissing);
        map.put(IS_OUTDATED, isOutdated);
        map.put(SEARCH_NAME, name);
        map.put(LOCALE, locale);
        return map;
    }
}
