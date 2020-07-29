package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Record;

public record Output(Record value, Array messages) {

  public Output {
    checkNotNull(messages);
  }

  public boolean hasValue() {
    return value != null;
  }

  public Record value() {
    checkState(hasValue(), "Output does not contain value.");
    return value;
  }
}
