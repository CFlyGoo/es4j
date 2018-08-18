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

package com.apehat.es4j.support;

import java.io.Serializable;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ThirdPartyAccount implements Serializable {

    static final String FIELD_PLATFORM_NAME = "platformName";
    static final String FIELD_THREAD_PARTY_ID = "thirdPartyId";
    private static final long serialVersionUID = -7636905641359890942L;
    private String platformName;
    private UserId thirdPartyId;

    ThirdPartyAccount(String platformName, UserId thirdPartyId) {
        this.platformName = platformName;
        this.thirdPartyId = thirdPartyId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public UserId getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(UserId thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }
}
