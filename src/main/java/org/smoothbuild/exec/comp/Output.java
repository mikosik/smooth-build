package org.smoothbuild.exec.comp;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;

public record Output(SObject value, Array messages) {

  public Output {
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
}
