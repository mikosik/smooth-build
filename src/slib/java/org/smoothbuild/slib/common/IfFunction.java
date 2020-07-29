package org.smoothbuild.slib.common;

import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class IfFunction {
  @SmoothFunction("if")
  public static Record ifFunction(NativeApi nativeApi, Bool condition, Record thenValue,
      Record elseValue) {
    if (condition.jValue()) {
      return thenValue;
    } else {
      return elseValue;
    }
  }

}
