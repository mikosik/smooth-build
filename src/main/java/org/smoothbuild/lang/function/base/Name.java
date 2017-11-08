package org.smoothbuild.lang.function.base;

import java.util.Objects;
import java.util.regex.Pattern;

public class Name {
  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_\\-0-9\\.]*");

  private final String value;

  public static boolean isLegalName(String simple) {
    return PATTERN.matcher(simple).matches();
  }

  public Name(String value) {
    if (!isLegalName(value)) {
      throw new IllegalArgumentException("Illegal name: '" + value + "'");
    }
    this.value = value;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Name && equals((Name) object);
  }

  private boolean equals(Name name) {
    return Objects.equals(value, name.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}
