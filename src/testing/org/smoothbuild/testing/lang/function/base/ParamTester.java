package org.smoothbuild.testing.lang.function.base;

import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ParamTester {
  public static ImmutableMap<String, Param> params(Param... params) {
    Builder<String, Param> builder = ImmutableMap.builder();
    for (Param param : params) {
      builder.put(param.name(), param);
    }
    return builder.build();
  }
}
