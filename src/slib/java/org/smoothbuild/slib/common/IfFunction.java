package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class IfFunction {
  @NativeImplementation("if")
  public static Obj ifFunction(NativeApi nativeApi, Bool condition, Obj thenValue,
      Obj elseValue) {
    if (condition.jValue()) {
      return thenValue;
    } else {
      return elseValue;
    }
  }

}
