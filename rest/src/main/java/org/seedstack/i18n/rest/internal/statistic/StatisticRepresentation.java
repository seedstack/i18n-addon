/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.statistic;

/**
 * @author PDC Date: 29/07/14
 */
public class StatisticRepresentation {

	private String locale;

	private int translated;

	private int totranslate;

	private int keytotal;

	private String englishLanguage;

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the translated
	 */
	public int getTranslated() {
		return translated;
	}

	/**
	 * @param translated
	 *            the translated to set
	 */
	public void setTranslated(int translated) {
		this.translated = translated;
	}

	/**
	 * @return the to translate
	 */
	public int getTotranslate() {
		return totranslate;
	}

	/**
	 * @param totranslate
	 *            the totranslate to set
	 */
	public void setTotranslate(int totranslate) {
		this.totranslate = totranslate;
	}

	/**
	 * @return the total key
	 */
	public int getKeytotal() {
		return keytotal;
	}

	/**
	 * @param keytotal
	 *            the keytotal to set
	 */
	public void setKeytotal(int keytotal) {
		this.keytotal = keytotal;
	}

	/**
	 * @return the englishLanguage
	 */
	public String getEnglishLanguage() {
		return englishLanguage;
	}

	/**
	 * @param englishLanguage
	 *            the englishLanguage to set
	 */
	public void setEnglishLanguage(String englishLanguage) {
		this.englishLanguage = englishLanguage;
	}

}
