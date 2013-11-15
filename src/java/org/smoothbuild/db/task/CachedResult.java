package org.smoothbuild.db.task;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.plugin.Value;

import com.google.common.collect.ImmutableList;

public class CachedResult {
  private final Value value;
  private final ImmutableList<Message> messages;

  public CachedResult(Value result, Iterable<Message> messages) {
    this.value = result;
    this.messages = ImmutableList.copyOf(messages);
  }

  public Value value() {
    return value;
  }

  public ImmutableList<Message> messages() {
    return messages;
  }
}
