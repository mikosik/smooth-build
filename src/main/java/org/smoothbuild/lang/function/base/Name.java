package org.smoothbuild.lang.function.base;

import java.util.Objects;
import java.util.regex.Pattern;

public class Name {
  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_\\-0-9\\.]*");

  private final String value;

  public static Name name(String value) {
    if (!isLegalName(value)) {
      throw new IllegalArgumentException("Illegal function name: '" + value + "'");
    }
    return new Name(value);
  }

  public static boolean isLegalName(String simple) {
    return PATTERN.matcher(simple).matches();
  }

  private Name(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Name && this.value.equals(((Name) object).value);
  }

  public boolean equals(Name name) {
    return Objects.equals(value, name.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "'" + value + "'";
  }
}
