package org.smoothbuild.exec.base;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;

public record Output(Obj value, Array messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasValue() {
    return value != null;
  }

  public Obj value() {
    checkState(hasValue(), "Output does not contain value.");
    return value;
  }
}
