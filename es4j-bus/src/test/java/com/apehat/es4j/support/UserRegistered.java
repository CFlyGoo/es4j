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
import java.util.Date;
import java.util.Objects;

/**
 * The class {@code UserRegistered} be used as event prototype
 *
 * @author hanpengfei
 * @since 1.0
 */
public final class UserRegistered implements Serializable {

    static final String FIELD_USER_ID = "userId";
    static final String FIELD_USERNAME = "username";
    static final String FIELD_REGISTER_ON = "registerOn";
    private static final long serialVersionUID = -3256210697855910724L;

    private UserId userId;
    private String username;
    private Date registerOn;

    UserRegistered(UserId userId, String username, Date registerOn) {
        this.userId = userId;
        this.username = username;
        this.registerOn = registerOn;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getRegisterOn() {
        return registerOn;
    }

    public void setRegisterOn(Date registerOn) {
        this.registerOn = registerOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRegistered that = (UserRegistered) o;
        return Objects.equals(userId, that.userId) &&
            Objects.equals(username, that.username) &&
            Objects.equals(registerOn, that.registerOn);
    }


    @Override
    public int hashCode() {
        return Objects.hash(userId, username, registerOn);
    }

    @Override
    public String toString() {
        return "UserRegistered{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", registerOn=" + registerOn +
            '}';
    }
}
