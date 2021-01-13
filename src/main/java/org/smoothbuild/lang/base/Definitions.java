package org.smoothbuild.lang.base;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<String, Defined> types,
    ImmutableMap<String, Defined> evaluables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of());
  }

  public static Definitions union(Definitions first, Definitions second) {
    return new Definitions(
        ImmutableMap.<String, Defined>builder()
            .putAll(first.types)
            .putAll(second.types)
            .build(),
        ImmutableMap.<String, Defined>builder()
            .putAll(first.evaluables)
            .putAll(second.evaluables)
            .build()
    );
  }

  public static Definitions baseTypeDefinitions() {
    ImmutableMap<String, Defined> baseTypes =
        BASE_TYPES.stream()
            .collect(toImmutableMap(Type::name, t -> new Defined(t, t.name(), internal())));
    return new Definitions(baseTypes, ImmutableMap.of());
  }
}
