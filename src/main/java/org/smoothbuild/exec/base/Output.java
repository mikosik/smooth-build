package org.smoothbuild.exec.base;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;

public record Output(Val value, Array messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasValue() {
    return value != null;
  }

  @Override
  public Val value() {
    checkState(hasValue(), "Output does not contain value.");
    return value;
  }
}
