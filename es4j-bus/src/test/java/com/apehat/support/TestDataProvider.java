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

package com.apehat.support;

import java.util.Date;
import java.util.UUID;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface TestDataProvider {

    String USER_REGISTERED_ID = UserRegistered.FIELD_USER_ID;
    String USER_REGISTERED_ID_ID = USER_REGISTERED_ID + '.' + UserId.FIELD_ID;
    String USER_REGISTERED_NAME = UserRegistered.FIELD_USERNAME;
    String USER_REGISTERED_TIME = UserRegistered.FIELD_REGISTER_ON;

    String THIRD_PARTY_ACCOUNT = UserRegisteredWithThirdPartyAccount.FIELD_ACCOUNT;
    String THIRD_PARTY_PLATFORM_NAME =
        THIRD_PARTY_ACCOUNT + '.' + ThirdPartyAccount.FIELD_PLATFORM_NAME;
    String THIRD_PART_ACCOUNT_ID =
        THIRD_PARTY_ACCOUNT + '.' + ThirdPartyAccount.FIELD_THREAD_PARTY_ID;
    String THIRD_PART_ACCOUNT_ID_ID = THIRD_PART_ACCOUNT_ID + '.' + UserId.FIELD_ID;

    String THIRD_PART_USER_REGISTERED = UserRegisteredWithThirdPartyAccount.FIELD_REGISTERED_EVENT;
    String THIRD_PART_USER_REGISTERED_ID = THIRD_PART_USER_REGISTERED + '.' + USER_REGISTERED_ID;
    String THIRD_PART_USER_REGISTERED_TIME =
        THIRD_PART_USER_REGISTERED + '.' + USER_REGISTERED_TIME;
    String THIRD_PART_USER_REGISTERED_NAME =
        THIRD_PART_USER_REGISTERED + '.' + USER_REGISTERED_NAME;
    String THIRD_PART_USER_REGISTERED_ID_ID = THIRD_PART_USER_REGISTERED_ID + '.' + UserId.FIELD_ID;

    static UserRegistered userRegisteredFixture() {
        UserId userId = new UserId(UUID.randomUUID().toString());
        String username = "testUsername";
        Date registerOn = new Date();
        return new UserRegistered(userId, username, registerOn);
    }

    static ThirdPartyAccount threadPartyAccountFixture() {
        String platformName = "testPlatform";
        UserId userId = new UserId(UUID.randomUUID().toString());
        return new ThirdPartyAccount(platformName, userId);
    }

    static UserRegisteredWithThirdPartyAccount userRegisteredWithThirdPartyAccountFixture() {
        return new UserRegisteredWithThirdPartyAccount(
            userRegisteredFixture(), threadPartyAccountFixture());
    }
}
