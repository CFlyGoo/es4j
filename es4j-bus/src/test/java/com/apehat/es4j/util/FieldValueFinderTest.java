/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apehat.es4j.util;

import static com.apehat.es4j.support.TestDataProvider.THIRD_PARTY_ACCOUNT;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PARTY_PLATFORM_NAME;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_ACCOUNT_ID;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_ACCOUNT_ID_ID;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_USER_REGISTERED;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_USER_REGISTERED_ID;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_USER_REGISTERED_ID_ID;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_USER_REGISTERED_NAME;
import static com.apehat.es4j.support.TestDataProvider.THIRD_PART_USER_REGISTERED_TIME;
import static com.apehat.es4j.support.TestDataProvider.userRegisteredWithThirdPartyAccountFixture;
import static org.testng.Assert.assertEquals;

import com.apehat.es4j.support.UserRegisteredWithThirdPartyAccount;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class FieldValueFinderTest {

    private UserRegisteredWithThirdPartyAccount obj;
    private FieldValueFinder finder;
    private Object[] exceptedData;

    @BeforeMethod
    public void setUp() {
        obj = userRegisteredWithThirdPartyAccountFixture();

        exceptedData = new Object[]{
            obj.getAccount(),
            obj.getAccount().getPlatformName(),
            obj.getAccount().getThirdPartyId(),
            obj.getAccount().getThirdPartyId().getId(),
            obj.getRegisteredEvent(),
            obj.getRegisteredEvent().getUserId(),
            obj.getRegisteredEvent().getRegisterOn(),
            obj.getRegisteredEvent().getUsername(),
            obj.getRegisteredEvent().getUserId().getId()
        };

        finder = new FieldValueFinder(obj);
    }

    @Test
    public void testLookupWithMultiLevelNestedObject() {
        startTest();
    }

    @Test
    public void testLookupWithNullData() {
        obj.getAccount().setThirdPartyId(null);
        exceptedData[2] = null;
        exceptedData[3] = null;
        obj.getRegisteredEvent().setRegisterOn(null);
        exceptedData[6] = null;
        obj.getRegisteredEvent().setUsername(null);
        exceptedData[7] = null;
        obj.getRegisteredEvent().getUserId().setId(null);
        exceptedData[8] = null;

        startTest();
    }

    /**
     * Watch cache by debug
     */
    @Test
    public void testLookupWithMultipleTimeForCache() {
        startTest();
        startTest();
    }

    private void startTest() {
        assertEquals(finder.lookup(THIRD_PARTY_ACCOUNT), exceptedData[0]);
        assertEquals(finder.lookup(THIRD_PARTY_PLATFORM_NAME), exceptedData[1]);
        assertEquals(finder.lookup(THIRD_PART_ACCOUNT_ID), exceptedData[2]);
        assertEquals(finder.lookup(THIRD_PART_ACCOUNT_ID_ID), exceptedData[3]);
        assertEquals(finder.lookup(THIRD_PART_USER_REGISTERED), exceptedData[4]);
        assertEquals(finder.lookup(THIRD_PART_USER_REGISTERED_ID), exceptedData[5]);
        assertEquals(finder.lookup(THIRD_PART_USER_REGISTERED_TIME), exceptedData[6]);
        assertEquals(finder.lookup(THIRD_PART_USER_REGISTERED_NAME), exceptedData[7]);
        assertEquals(finder.lookup(THIRD_PART_USER_REGISTERED_ID_ID), exceptedData[8]);
    }
}