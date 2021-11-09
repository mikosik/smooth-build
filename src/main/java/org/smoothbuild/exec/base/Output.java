package org.smoothbuild.exec.base;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;

public record Output(ValueH value, ArrayH messages) {

  public Output {
    requireNonNull(messages);
  }

  public boolean hasValue() {
    return value != null;
  }

  @Override
  public ValueH value() {
    checkState(hasValue(), "Output does not contain value.");
    return value;
  }
}
