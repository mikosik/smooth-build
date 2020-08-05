package org.smoothbuild.lang.parse;

import org.smoothbuild.lang.base.Evaluable;
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
}
