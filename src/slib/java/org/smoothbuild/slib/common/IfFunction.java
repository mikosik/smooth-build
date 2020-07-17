package org.smoothbuild.slib.common;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.Record;

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
