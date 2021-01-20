package org.smoothbuild.lang.base.define;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<String, Defined> types,
    ImmutableMap<String, Referencable> referencables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of());
  }

  public static Definitions union(Definitions first, Definitions second) {
    return new Definitions(
        ImmutableMap.<String, Defined>builder()
            .putAll(first.types)
            .putAll(second.types)
            .build(),
        ImmutableMap.<String, Referencable>builder()
            .putAll(first.referencables)
            .putAll(second.referencables)
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
