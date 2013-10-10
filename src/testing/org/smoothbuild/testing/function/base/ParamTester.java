package org.smoothbuild.testing.function.base;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class ParamTester {
  public static Param param(Type type, String name) {
    return param(type, name, false);
  }

  public static Param param(Type type, String name, boolean isRequired) {
    return Param.param(type, name, isRequired, HashCode.fromInt(0));
  }

  public static ImmutableMap<String, Param> params(Param... params) {
    Builder<String, Param> builder = ImmutableMap.builder();
    for (Param param : params) {
      builder.put(param.name(), param);
    }
    return builder.build();
  }
}
