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

package com.apehat.es4j.bus.disptach;

import com.apehat.es4j.bus.event.PendingEvent;
import com.apehat.es4j.bus.subscriber.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class AsyncDispatcher {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncDispatcher.class);
  private final ExecutorService pool;
  
  public AsyncDispatcher(ExecutorService pool) {
    this.pool = Objects.requireNonNull(pool, "Pool must not be null");
  }
  
  public void dispatch(PendingEvent event, Set<Subscriber> subscribers) {
    if (subscribers.isEmpty()) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn("Non subscriber for " + event);
      }
      return;
    }
    AsyncDispatchTask task = new AsyncDispatchTask(subscribers, event);
    pool.submit(task);
  }
  
  private static class AsyncDispatchTask implements Runnable {
    
    private final Set<Subscriber> subscribers;
    private final PendingEvent event;
    
    AsyncDispatchTask(Set<Subscriber> subscribers, PendingEvent event) {
      assert event != null;
      assert subscribers != null;
      assert !subscribers.isEmpty();
      this.subscribers = subscribers;
      this.event = event;
    }
    
    @Override
    public void run() {
      for (Subscriber subscriber : subscribers) {
        try {
          subscriber.onEvent(event);
        } catch (RuntimeException e) {
          LOGGER.warn("Async dispatch failure", e);
        }
      }
    }
  }
}
