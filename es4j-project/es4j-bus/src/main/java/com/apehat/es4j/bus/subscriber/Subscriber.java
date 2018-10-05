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

package com.apehat.es4j.bus.subscriber;

import com.apehat.es4j.bus.EventHandler;
import com.apehat.es4j.bus.event.PendingEvent;

import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class Subscriber {
  
  /*
   * 1. 检查方法是否属于一个静态方法，如果属于，视为无状态对象
   * 2. 检查方法所属类是否属于一个无状态类
   * 3. 为有状态对象建立表示，并将处理方法与该标识绑定
   * 4. 将被标识的对象进行内存跟踪
   * 5. 当进行调用的时候，检查该处理其是否属于一个被内存跟踪的对象，如果属于，向内存跟踪器发出请求，之后再进行调用
   * 6. 否则，进行直接的调用
   */
  
  private final EventHandler handler;
  private final long subscriptionOn;
  private final Type type;
  
  Subscriber(EventHandler handler, Type type) {
    this.subscriptionOn = System.currentTimeMillis();
    this.handler = Objects.requireNonNull(handler, "Handler must not be null");
    this.type = Objects.requireNonNull(type, "Subscription type must not be null.");
  }
  
  public void onEvent(PendingEvent event) {
    if (!isSubscribed(event.type())) {
      throw new IllegalArgumentException("Hadn't subscription to " + event.type());
    }
    if (subscriptionOn > event.occurredOn()) {
      throw new IllegalArgumentException("Event already occurred");
    }
    handler.onEvent(event.toEvent());
  }
  
  public boolean isSubscribed(Class<?> cls) {
    return this.type.isAssignableFrom(cls);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Subscriber that = (Subscriber) o;
    return Objects.equals(handler, that.handler);
  }
  
  @Override
  public int hashCode() {
    int result = 191;
    result = 31 * result + handler.hashCode();
    return result;
  }
}
