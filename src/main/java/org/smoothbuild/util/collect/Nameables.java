package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;

public class Nameables {
  public static <E extends Nameable> ImmutableMap<String, E> toMap(
      Collection<? extends E> values) {
    return values.stream()
        .filter(v -> v.nameO().isPresent())
        .collect(toImmutableMap(v -> v.nameO().get(), v -> v));
  }
}
