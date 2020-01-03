package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;

/**
 * This class is immutable.
 */
public class Output {
  private final SObject value;
  private final Array messages;

  public Output(SObject value, Array messages) {
    this.value = value;
    this.messages = checkNotNull(messages);
  }

  public boolean hasValue() {
    return value != null;
  }

  public SObject value() {
    checkState(hasValue(), "Output does not contain value.");
    return value;
  }

  public Array messages() {
    return messages;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Output && equals((Output) object);
  }

  private boolean equals(Output output) {
    return Objects.equals(value, output.value)
        && Objects.equals(messages, output.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, messages);
  }

  @Override
  public String toString() {
    return "TaskOutput(" + value + ", " + messages + ")";
  }
}
