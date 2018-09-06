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

import java.io.Serializable;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class UserRegisteredWithThirdPartyAccount implements Serializable {

    static final String FIELD_REGISTERED_EVENT = "registeredEvent";
    static final String FIELD_ACCOUNT = "account";
    private static final long serialVersionUID = 1244066911786017876L;
    private UserRegistered registeredEvent;
    private ThirdPartyAccount account;

    UserRegisteredWithThirdPartyAccount(
        UserRegistered registeredEvent, ThirdPartyAccount account) {
        this.registeredEvent = registeredEvent;
        this.account = account;
    }

    public UserRegistered getRegisteredEvent() {
        return registeredEvent;
    }

    public void setRegisteredEvent(UserRegistered registeredEvent) {
        this.registeredEvent = registeredEvent;
    }

    public ThirdPartyAccount getAccount() {
        return account;
    }

    public void setAccount(ThirdPartyAccount account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        UserRegisteredWithThirdPartyAccount that = (UserRegisteredWithThirdPartyAccount) object;
        return Objects.equals(registeredEvent, that.registeredEvent) &&
            Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredEvent, account);
    }

    @Override
    public String toString() {
        return "UserRegisteredWithThirdPartyAccount{" +
            "registeredEvent=" + registeredEvent +
            ", THIRD_PARTY_ACCOUNT=" + account +
            '}';
    }
}
