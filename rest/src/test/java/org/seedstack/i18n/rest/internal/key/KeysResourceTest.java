/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;

import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.shared.BadRequestException;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class KeysResourceTest {

    @Tested
    private KeysResource underTest;

    @Injectable
    private KeyFinder keyFinder;
    @Injectable
    private KeyRepository keyRepository;
    @Injectable
    private KeyFactory keyFactory;

    @Test(expected = BadRequestException.class)
    public void testGetKeysPageSizeShouldBeGreaterThanZero() {
        underTest.getKeys(0L,0,true, true, true, "");
    }
}
