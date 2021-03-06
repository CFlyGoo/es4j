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

package com.apehat.es4j.bus.port.adapter;

import com.apehat.Validation;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
final class EventHandlerStaticMethodAdapter extends AbstractEventHandlerMethodAdapter {
  
  EventHandlerStaticMethodAdapter(Method method) {
    super(Validation.requiredStatic(method));
  }
  
  @Override
  protected Object getInvoker() {
    return null;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || o.getClass() != this.getClass()) {
      return false;
    }
    EventHandlerStaticMethodAdapter that = (EventHandlerStaticMethodAdapter) o;
    return Objects.equals(getHandler(), that.getHandler());
  }
  
  @Override
  public int hashCode() {
    int result = 229;
    result = 31 * result + getHandler().hashCode();
    return result;
  }
}
