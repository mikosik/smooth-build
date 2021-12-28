package org.smoothbuild.exec.base;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.ValB;

public record Output(ValB val, ArrayB messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasValue() {
    return val != null;
  }

  @Override
  public ValB val() {
    checkState(hasValue(), "Output does not contain value.");
    return val;
  }
}
