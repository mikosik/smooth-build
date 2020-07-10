package org.smoothbuild.parse;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<String, Type> types,
    ImmutableMap<String, Callable> callables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of());
  }

  public static Definitions union(Definitions first, Definitions second) {
    return new Definitions(
        ImmutableMap.<String, Type>builder()
            .putAll(first.types)
            .putAll(second.types)
            .build(),
        ImmutableMap.<String, Callable>builder()
            .putAll(first.callables)
            .putAll(second.callables)
            .build()
    );
  }
}
