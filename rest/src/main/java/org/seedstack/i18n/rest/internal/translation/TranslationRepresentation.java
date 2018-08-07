/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;

/**
 * @author pierre.thirouin@ext.mpsa.com
 * Date: 21/11/13
 */
public class TranslationRepresentation {

    private String name;

    private String comment;

    private TranslationValueRepresentation source;
    
    private TranslationValueRepresentation target;
    
    private boolean missing;

    public TranslationRepresentation() {
    }

    public TranslationRepresentation(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

    public TranslationValueRepresentation getSource() {
        return source;
    }

    public void setSource(TranslationValueRepresentation source) {
        this.source = source;
    }

    public TranslationValueRepresentation getTarget() {
        return target;
    }

    public void setTarget(TranslationValueRepresentation target) {
        this.target = target;
    }

    /**
	 * @return the missing
	 */
	public boolean isMissing() {
		return missing;
	}

	/**
	 * @param missing the missing to set
	 */
	public void setMissing(boolean missing) {
		this.missing = missing;
	}
    
}
