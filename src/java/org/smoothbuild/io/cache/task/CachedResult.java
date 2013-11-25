package org.smoothbuild.io.cache.task;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;

public class CachedResult {
  private final SValue value;
  private final ImmutableList<Message> messages;

  public CachedResult(SValue result, Iterable<Message> messages) {
    this.value = result;
    this.messages = ImmutableList.copyOf(messages);
  }

  public SValue value() {
    return value;
  }

  public ImmutableList<Message> messages() {
    return messages;
  }
}
