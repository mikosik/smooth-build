package org.smoothbuild.parse;

import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.type.Type;

import com.google.common.collect.ImmutableMap;

public record Defined(
    ImmutableMap<String, Type> types,
    ImmutableMap<String, Function> functions) {

  public static Defined empty() {
    return new Defined(ImmutableMap.of(), ImmutableMap.of());
  }

  public static Defined union(Defined first, Defined second) {
    return new Defined(
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
