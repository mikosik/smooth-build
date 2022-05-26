package org.smoothbuild.vm.algorithm;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;

public record Output(CnstB cnst, ArrayB messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasValue() {
    return cnst != null;
  }

  public CnstB cnst() {
    checkState(hasValue(), "Output does not contain value.");
    return cnst;
  }
}
