package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ThrowException {
  @NativeImplementation("throwException")
  public static Record throwException(NativeApi nativeApi) {
    throw new UnsupportedOperationException();
  }
}
