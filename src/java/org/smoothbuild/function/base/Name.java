package org.smoothbuild.function.base;

import java.util.regex.Pattern;

import com.google.common.base.Splitter;

public class Name {
  private static final char SEPARATOR = '.';
  private static final Pattern PART_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");

  private final String aPackage;
  private final String simple;
  private final String full;

  public static Name simpleName(String simple) {
    if (!isLegalSimpleName(simple)) {
      throw new IllegalArgumentException("Illegal function name: '" + simple + "'");
    }
    return new Name("", simple, simple);
  }

  public static boolean isLegalSimpleName(String simple) {
    return isLegalPart(simple);
  }

  public static Name qualifiedName(String qualified) {
    for (String part : Splitter.on(SEPARATOR).split(qualified)) {
      if (!isLegalPart(part)) {
        throw new IllegalArgumentException("Illegal qualified function name: '" + qualified
            + "'");
      }
    }
    int index = qualified.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return new Name("", qualified, qualified);
    } else {
      String aPackage = qualified.substring(0, index);
      String name = qualified.substring(index + 1);
      return new Name(aPackage, name, qualified);
    }
  }

  private static boolean isLegalPart(String part) {
    return PART_PATTERN.matcher(part).matches();
  }

  private Name(String aPackage, String simple, String full) {
    this.aPackage = aPackage;
    this.simple = simple;
    this.full = full;
  }

  public String aPackage() {
    return aPackage;
  }

  public String simple() {
    return simple;
  }

  public String full() {
    return full;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof Name)) {
      return false;
    }
    Name that = (Name) object;
    return this.full.equals(that.full);
  }

  @Override
  public final int hashCode() {
    return full.hashCode();
  }

  @Override
  public String toString() {
    return "'" + full + "'";
  }
}
