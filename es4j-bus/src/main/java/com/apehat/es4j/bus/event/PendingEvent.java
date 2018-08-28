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

package com.apehat.es4j.bus.event;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class PendingEvent {

    /*
     * 事件采用内存跟踪与持久化并行机制
     * 查询一个事件的时候，先将事件缓存到内存跟踪器中，再进行返回，以便随时观察事件的被消费状态
     *
     * 现在需要确定的是，什么时候结束对对象的内存跟踪
     *
     * 1. 计数跟踪
     * 一种办法是，要求调用者显式进行对象的存储操作，跟踪器持有对对象查询引用的计数，只有计数为0时，
     * 才真正将对象进行持久化
     * 这种办法的问题在与
     * 对象内存跟踪的问题在与，一般情况下，不能再依赖于持久化的并发处理机制，并发处于由被访问对象承担
     *
     * 2. 版本跟踪
     * 为对象添加版本号，并在（写）操作时，检查对象版本是否与当前版本一致。（参考 MVCC)
     */

    private Event metadata;

    PendingEvent(Object prototype, String source) {
        this.metadata = new Event(System.currentTimeMillis(), prototype, source);
    }

    public Event toEvent() {
        return metadata;
    }

    public long occurredOn() {
        return metadata.occurredOn();
    }

    public Class<?> type() {
        return metadata.type();
    }
}
