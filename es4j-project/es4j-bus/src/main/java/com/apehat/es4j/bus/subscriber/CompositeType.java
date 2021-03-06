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

import com.apehat.AbstractClassItem;
import com.apehat.ClassItemCombiner;
import com.apehat.Item;

import java.util.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class CompositeType implements Type {
  
  private static final long serialVersionUID = -467998764902780604L;
  
  private final Set<Item<Class<?>>> items;
  
  public CompositeType(Class<?>... types) {
    this(items(types));
  }
  
  private CompositeType(Set<Item<Class<?>>> items) {
    this.items = Collections.unmodifiableSet(new ClassItemCombiner(items).rebuild());
  }
  
  private static Set<Item<Class<?>>> items(Class<?>... types) {
    if (types == null || types.length == 0) {
      throw new IllegalArgumentException("Must specified types");
    }
    final Set<Class<?>> set = new HashSet<>(Arrays.asList(types));
    final Set<Item<Class<?>>> items = new HashSet<>();
    for (Class<?> cls : set) {
      items.add(new IncludeType(cls));
    }
    return items;
  }
  
  @Override
  public Type add(Class<?> type) {
    if (isAssignableFrom(type)) {
      return this;
    }
    final Set<Item<Class<?>>> newItems = new HashSet<>();
    boolean added = false;
    for (Item<Class<?>> item : items) {
      if (item.value().isAssignableFrom(type)) {
        newItems.add(item.add(type));
        added = true;
      }
    }
    if (!added) {
      newItems.add(new IncludeType(type));
    }
    return new CompositeType(newItems);
  }
  
  @Override
  public Type remove(Class<?> type) {
    if (!isAssignableFrom(type)) {
      return this;
    }
    final Set<Item<Class<?>>> newItems = new HashSet<>();
    for (Item<Class<?>> item : items) {
      if (item.value() == type) {
        continue;
      }
      if (item.value().isAssignableFrom(type)) {
        newItems.add(item.remove(type));
      } else {
        newItems.add(item);
      }
    }
    return new CompositeType(newItems);
  }
  
  @Override
  public boolean isAssignableFrom(Class<?> cls) {
    for (Item<Class<?>> item : items) {
      if (item.contains(cls)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompositeType that = (CompositeType) o;
    return Objects.equals(items, that.items);
  }
  
  @Override
  public int hashCode() {
    int hash = 157;
    hash += 31 * hash + items.hashCode();
    return hash;
  }
  
  @Override
  public String toString() {
    return "CompositeType{" +
            "items=" + items +
            '}';
  }
  
  private static final class ExceptedType extends AbstractClassItem {
    
    private static final long serialVersionUID = 1921985160179831733L;
    
    ExceptedType(Class<?> value) {
      super(value);
    }
    
    private ExceptedType(Class<?> value, Set<Item<Class<?>>> slots) {
      super(value, new ClassItemCombiner(slots).rebuild());
    }
    
    @Override
    public Item<Class<?>> newInstance(Class<?> value, Set<Item<Class<?>>> slots) {
      return new ExceptedType(value, slots);
    }
    
    @Override
    public Item<Class<?>> newReverseInstance(Class<?> value) {
      return new IncludeType(value);
    }
    
    @Override
    public boolean isEnable() {
      return false;
    }
  }
  
  private static final class IncludeType extends AbstractClassItem {
    
    private static final long serialVersionUID = -1321655907319211783L;
    
    IncludeType(Class<?> value) {
      super(value);
    }
    
    private IncludeType(Class<?> value, Set<Item<Class<?>>> slots) {
      super(value, new ClassItemCombiner(slots).rebuild());
    }
    
    @Override
    public Item<Class<?>> newInstance(Class<?> value, Set<Item<Class<?>>> slots) {
      return new IncludeType(value, slots);
    }
    
    @Override
    public Item<Class<?>> newReverseInstance(Class<?> value) {
      return new ExceptedType(value);
    }
    
    @Override
    public boolean isEnable() {
      return true;
    }
  }
}
