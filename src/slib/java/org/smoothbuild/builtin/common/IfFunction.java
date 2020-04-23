package org.smoothbuild.builtin.common;

import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

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
