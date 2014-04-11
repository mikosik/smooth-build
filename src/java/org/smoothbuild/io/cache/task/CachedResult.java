package org.smoothbuild.io.cache.task;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;

public class CachedResult<T extends SValue> {
  private final T value;
  private final ImmutableList<Message> messages;

  public CachedResult(T result, Iterable<Message> messages) {
    this.value = result;
    this.messages = ImmutableList.copyOf(messages);
  }

  public T value() {
    return value;
  }

  public ImmutableList<Message> messages() {
    return messages;
  }
}
