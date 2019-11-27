package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;

public class Output {
  private final SObject result;
  private final Array messages;

  public Output(SObject result, Array messages) {
    this.result = result;
    this.messages = checkNotNull(messages);
  }

  public boolean hasResult() {
    return result != null;
  }

  public SObject result() {
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
