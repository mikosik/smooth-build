package org.smoothbuild.db.taskresults;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;

public class TaskResult<T extends SValue> {
  private final T value;
  private final ImmutableList<Message> messages;

  public TaskResult(T result, Iterable<Message> messages) {
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
