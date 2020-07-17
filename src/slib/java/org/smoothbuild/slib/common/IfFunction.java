package org.smoothbuild.slib.common;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.SObject;

public class IfFunction {
  @SmoothFunction("if")
  public static SObject ifFunction(NativeApi nativeApi, Bool condition, SObject thenValue,
      SObject elseValue) {
    if (condition.jValue()) {
      return thenValue;
    } else {
      return elseValue;
    }
  }

}
