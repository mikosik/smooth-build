package org.smoothbuild.db.taskresults;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;

public class TaskResult<T extends SValue> {
  private final T value;
  private final ImmutableList<Message> messages;

  public TaskResult(T value, Iterable<Message> messages) {
    this.value = checkNotNull(value);
    this.messages = ImmutableList.copyOf(messages);
  }

  public TaskResult(Iterable<Message> messages) {
    this.value = null;
    this.messages = ImmutableList.copyOf(messages);
  }

  public boolean hasValue() {
    return value != null;
  }

  public T value() {
    checkState(hasValue(), "TaskResult does not contain any value.");
    return value;
  }

  public ImmutableList<Message> messages() {
    return messages;
  }
}
