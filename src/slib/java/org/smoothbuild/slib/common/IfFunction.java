package org.smoothbuild.slib.common;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.plugin.NativeApi;

public class IfFunction {
  public static Obj function(NativeApi nativeApi, Bool condition, Obj thenValue,
      Obj elseValue) {
    if (condition.jValue()) {
      return thenValue;
    } else {
      return elseValue;
    }
  }

}
