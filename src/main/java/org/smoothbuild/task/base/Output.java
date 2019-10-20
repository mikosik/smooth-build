package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class Output {
  private final Value result;
  private final Array messages;

  public Output(Value result, Array messages) {
    this.result = result;
    this.messages = checkNotNull(messages);
  }

  public boolean hasResult() {
    return result != null;
  }

  public Value result() {
    checkState(hasResult(), "Output does not contain result.");
    return result;
  }

  public Array messages() {
    return messages;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Output && equals((Output) object);
  }

  private boolean equals(Output output) {
    return Objects.equals(result, output.result)
        && Objects.equals(messages, output.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, messages);
  }

  @Override
  public String toString() {
    return "TaskOutput(" + result + ", " + messages + ")";
  }
}
