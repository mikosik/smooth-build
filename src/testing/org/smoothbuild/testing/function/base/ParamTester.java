package org.smoothbuild.testing.function.base;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

import com.google.common.hash.HashCode;

public class ParamTester {
  public static Param param(Type type, String name) {
    return param(type, name, false);
  }

  public static Param param(Type type, String name, boolean isRequired) {
    return Param.param(type, name, isRequired, HashCode.fromInt(0));
  }
}
