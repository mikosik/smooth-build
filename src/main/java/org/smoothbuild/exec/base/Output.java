package org.smoothbuild.exec.base;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ValH;

public record Output(ValH val, ArrayH messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasValue() {
    return val != null;
  }

  @Override
  public ValH val() {
    checkState(hasValue(), "Output does not contain value.");
    return val;
  }
}
