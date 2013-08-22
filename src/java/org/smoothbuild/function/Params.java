package org.smoothbuild.function;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Params {
  private final ImmutableMap<String, Param> map;

  public static Params params(Param... params) {
    return new Params(params);
  }

  public Params(Param... params) {
    this.map = createMap(params);
  }

  public Param param(String name) {
    return map.get(name);
  }

  private static ImmutableMap<String, Param> createMap(Param... params) {
    Builder<String, Param> builder = ImmutableMap.builder();
    for (Param param : params) {
      builder.put(param.name(), param);
    }
    return builder.build();
  }

  @Override
  public String toString() {
    return "Params(" + Joiner.on(", ").join(map.values()) + ")";
  }
}
