package org.smoothbuild.builtin.common;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.Value;

public class IfFunction {
  @SmoothFunction("if")
  public static Value ifFunction(NativeApi nativeApi, Bool condition, Value thenValue,
      Value elseValue) throws IOException {
    if (condition.data()) {
      return thenValue;
    } else {
      return elseValue;
    }
  }

}
