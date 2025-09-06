package org.smoothbuild.common.base;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.Nullable;

public class Check {
  public static <T> T checkInitializedToNotNull(@Nullable T value, String name) {
    return requireNonNull(value, Strings.q(name) + " has not been initialized yet.");
  }
}
