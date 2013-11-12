package org.smoothbuild.function.base;

import java.util.regex.Pattern;

public class Name {
  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9\\.]*");

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
  public final boolean equals(Object object) {
    if (!(object instanceof Name)) {
      return false;
    }
    Name that = (Name) object;
    return this.value.equals(that.value);
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "'" + value + "'";
  }
}
