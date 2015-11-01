package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MessageStats {
  private final ImmutableMap<MessageType, AtomicInteger> map = createMap();

  public void incCount(MessageType messageType) {
    map.get(messageType).incrementAndGet();
  }

  public int getCount(MessageType messageType) {
    return map.get(messageType).get();
  }

  public boolean containsErrors() {
    return 0 < getCount(ERROR);
  }

  public void add(MessageStats messageStats) {
    for (Entry<MessageType, AtomicInteger> entry : map.entrySet()) {
      entry.getValue().addAndGet(messageStats.getCount(entry.getKey()));
    }
  }

  private static ImmutableMap<MessageType, AtomicInteger> createMap() {
    HashMap<MessageType, AtomicInteger> map = Maps.newHashMap();
    for (MessageType messageType : MessageType.values()) {
      map.put(messageType, new AtomicInteger(0));
    }
    return Maps.immutableEnumMap(map);
  }
}
