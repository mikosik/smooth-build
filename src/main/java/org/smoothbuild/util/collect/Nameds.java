package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;

public class Nameds {
  public static <E extends Named> ImmutableMap<String, E> toMap(Collection<? extends E> elements) {
    return elements.stream()
        .collect(toImmutableMap(Named::name, v -> v));
  }
}
