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

package com.apehat.es4j.bus.support;

import com.apehat.es4j.bus.Type;
import com.apehat.es4j.util.graph.AcmeDirectedGraph;
import com.apehat.es4j.util.graph.DirectedGraph;
import com.apehat.es4j.util.graph.Indicator;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class CompositeType implements Type {

    private static final long serialVersionUID = 3219303655921329611L;

    private final Indicator<Class<?>> indicator = (o1, o2) -> o2.isAssignableFrom(o1);

    private Set<OpenItem> items;

    public CompositeType(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            throw new IllegalArgumentException("Must specified class");
        }
        this.items = new HashSet<>();
        for (Class<?> cls : classes) {
            doAdd(cls, items);
        }
    }

    public void add(Class<?> cls) {
        if (isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Already contains " + cls);
        }
        doAdd(cls, items);
    }

    public void remove(Class<?> cls) {
        if (!isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Hadn't registered with " + cls);
        }
        doRemove(cls, items);
    }

    private void doAdd(Class<?> cls, Set<OpenItem> items) {
        boolean added = false;
        for (OpenItem item : items) {
            if (item.value() == cls) {
                item.clearSlots();
                added = true;
            } else if (item.value().isAssignableFrom(cls)) {
                item.add(cls);
                added = true;
            }
        }
        if (!added) {
            items.add(new OpenItem(cls));
            clear(items);
        }
    }

    private void clear(Set<? extends Item> items) {
        if (items.isEmpty()) {
            return;
        }
        Set<Class<?>> itemValues = new HashSet<>();
        for (Item item : items) {
            itemValues.add(item.value());
        }
        DirectedGraph<Class<?>> graph = new AcmeDirectedGraph<>(itemValues, indicator);

        Set<Class<?>> top = graph.getTop();
        if (top.size() != itemValues.size()) {
            // 说明有可以合并项目
            // 获取被移除的项目
            Set<Class<?>> temp = graph.items();
            temp.removeAll(top);
            // temp 中保存了所有可以被合并的子项目
            Set<Item> mergeableItems = new HashSet<>();
            for (Item item : items) {
                if (temp.contains(item.value())) {
                    mergeableItems.add(item);
                }
            }
            // 保留项
            //noinspection SuspiciousMethodCalls
            items.removeAll(mergeableItems);

            // 通过可合并项的value，获取能合并到哪些项
            // 之后将可合并项的槽点合并到他的超项
            for (Item item : mergeableItems) {
                Set<Class<?>> reachableSet = graph.getReachableSet(item.value());
                for (Item openItem : items) {
                    if (reachableSet.contains(openItem.value())) {
                        openItem.mount(item.slots());
                    }
                }
            }
        }
    }

    private void doRemove(Class<?> cls, Set<OpenItem> openItems) {
        HashSet<OpenItem> pendingRemoves = new HashSet<>();
        for (OpenItem openItem : openItems) {
            Class<?> value = openItem.value();
            if (value == cls) {
                pendingRemoves.add(openItem);
            } else if (openItem.contains(cls)) {
                openItem.remove(cls);
            }
        }
        openItems.removeAll(pendingRemoves);
    }

    @Override
    public boolean isAssignableFrom(Class<?> cls) {
        for (Item item : items) {
            if (item.contains(cls)) {
                return true;
            }
        }
        return false;
    }

    private interface Item {

        void add(Class<?> cls);

        void remove(Class<?> cls);

        Class<?> value();

        boolean isOpen();

        boolean contains(Class<?> cls);

        void clearSlots();

        void mount(Set<? extends Item> items);

        Set<? extends Item> slots();
    }

    private class OpenItem implements Item {

        private final Class<?> value;
        private final Set<CloseItem> closeItems;

        private OpenItem(Class<?> value) {
            this.value = value;
            this.closeItems = new HashSet<>();
        }

        @Override
        public void add(Class<?> cls) {
            assert cls != value;
            Set<CloseItem> pendingRemoves = new HashSet<>();
            for (CloseItem closeItem : closeItems) {
                if (closeItem.value() == cls) {
                    pendingRemoves.add(closeItem);
                } else if (closeItem.contains(cls)) {
                    closeItem.add(cls);
                }
            }
            closeItems.removeAll(pendingRemoves);
        }

        @Override
        public void remove(Class<?> cls) {
            assert cls != value();
            boolean flag = false;
            for (CloseItem closeItem : closeItems) {
                Class<?> value = closeItem.value();
                if (value == cls) {
                    closeItem.clearSlots();
                    flag = true;
                } else if (value.isAssignableFrom(cls)) {
                    closeItem.remove(cls);
                    flag = true;
                }
            }
            if (!flag) {
                closeItems.add(new CloseItem(cls));
                clear(closeItems);
            }
        }

        @Override
        public Class<?> value() {
            return value;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public boolean contains(Class<?> cls) {
            if (!value.isAssignableFrom(cls)) {
                return false;
            }
            if (cls == value) {
                return true;
            }
            for (CloseItem closeItem : closeItems) {
                if (closeItem.contains(cls)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void clearSlots() {
            this.closeItems.clear();
        }

        @Override
        public void mount(Set<? extends Item> items) {
            for (Item item : items) {
                assert item instanceof CloseItem;
                closeItems.add((CloseItem) item);
            }
            clear(closeItems);
        }

        @Override
        public Set<? extends Item> slots() {
            return closeItems;
        }

    }


    private class CloseItem implements Item {

        private final Class<?> value;
        private final Set<OpenItem> openItems;

        private CloseItem(Class<?> value) {
            this.value = value;
            this.openItems = new HashSet<>();
        }

        @Override
        public void add(Class<?> cls) {
            assert cls != value();
            doAdd(cls, openItems);
        }

        @Override
        public void clearSlots() {
            this.openItems.clear();
        }

        @Override
        public void mount(Set<? extends Item> items) {
            for (Item item : items) {
                assert item instanceof OpenItem;
                openItems.add((OpenItem) item);
            }
            clear(openItems);
        }

        @Override
        public Set<? extends Item> slots() {
            return openItems;
        }

        @Override
        public void remove(Class<?> cls) {
            assert cls != value;
            doRemove(cls, openItems);
        }

        @Override
        public Class<?> value() {
            return value;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean contains(Class<?> cls) {
            if (!value.isAssignableFrom(cls)) {
                return false;
            }
            if (value == cls) {
                return true;
            }
            for (OpenItem openItem : openItems) {
                if (openItem.contains(cls)) {
                    return false;
                }
            }
            return true;
        }
    }
}
