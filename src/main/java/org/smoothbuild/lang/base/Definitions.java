package org.smoothbuild.lang.base;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.type.Types.BASIC_TYPES;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<String, Type> types,
    ImmutableMap<String, Evaluable> evaluables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of());
  }

  public static Definitions union(Definitions first, Definitions second) {
    return new Definitions(
        ImmutableMap.<String, Type>builder()
            .putAll(first.types)
            .putAll(second.types)
            .build(),
        ImmutableMap.<String, Evaluable>builder()
            .putAll(first.evaluables())
            .putAll(second.evaluables())
            .build()
    );
  }

  public static Definitions basicTypeDefinitions() {
    return new Definitions(
        BASIC_TYPES.stream().collect(toImmutableMap(Type::name, t -> t)),
        ImmutableMap.of());
  }
}
