package org.smoothbuild.message.message;

import java.util.HashMap;
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

  private static ImmutableMap<MessageType, AtomicInteger> createMap() {
    HashMap<MessageType, AtomicInteger> map = Maps.newHashMap();
    for (MessageType messageType : MessageType.values()) {
      map.put(messageType, new AtomicInteger(0));
    }
    return Maps.immutableEnumMap(map);
  }
}
