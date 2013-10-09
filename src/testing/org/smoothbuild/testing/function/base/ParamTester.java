package org.smoothbuild.testing.function.base;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

public class ParamTester {
  public static Param param(Type type, String name) {
    return Param.param(type, name, false);
  }
}
