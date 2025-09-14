package org.smoothbuild.common.base;

import static java.util.Objects.requireNonNull;

public class Check {
  public static <T> T checkInitializedToNotNull(T value, String name) {
    return requireNonNull(value, Strings.q(name) + " has not been initialized yet.");
  }
}
