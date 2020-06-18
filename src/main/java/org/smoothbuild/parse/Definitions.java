package org.smoothbuild.parse;

import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.type.Type;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<String, Type> types,
    ImmutableMap<String, Function> functions) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of());
  }

  public static Definitions union(Definitions first, Definitions second) {
    return new Definitions(
        ImmutableMap.<String, Type>builder()
            .putAll(first.types)
            .putAll(second.types)
            .build(),
        ImmutableMap.<String, Function>builder()
            .putAll(first.functions)
            .putAll(second.functions)
            .build()
    );
  }
}
