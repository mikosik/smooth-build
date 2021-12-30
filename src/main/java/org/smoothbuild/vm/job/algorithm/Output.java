package org.smoothbuild.vm.job.algorithm;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;

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
