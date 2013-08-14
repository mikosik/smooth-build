package org.smoothbuild.lang.function;

import java.util.regex.Pattern;

import com.google.common.base.Splitter;

public class CanonicalName {
  private static final char SEPARATOR = '.';
  private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");

  private final String aPackage;
  private final String simple;
  private final String full;

  public static CanonicalName simpleName(String simple) {
    if (!isValidSimpleName(simple)) {
      throw new IllegalArgumentException("Illegal function name: '" + simple + "'");
    }
    return new CanonicalName("", simple, simple);
  }

  public static boolean isValidSimpleName(String simple) {
    return isValidName(simple);
  }

  public static CanonicalName canonicalName(String canonicalName) {
    for (String part : Splitter.on(SEPARATOR).split(canonicalName)) {
      if (!isValidName(part)) {
        throw new IllegalArgumentException("Illegal canonical function name: '" + canonicalName
            + "'");
      }
    }
    int index = canonicalName.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return new CanonicalName("", canonicalName, canonicalName);
    } else {
      String aPackage = canonicalName.substring(0, index);
      String name = canonicalName.substring(index + 1);
      return new CanonicalName(aPackage, name, canonicalName);
    }
  }

  private static boolean isValidName(String name) {
    return NAME_PATTERN.matcher(name).matches();
  }

  private CanonicalName(String aPackage, String simple, String full) {
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
    if (!(object instanceof CanonicalName)) {
      return false;
    }
    CanonicalName that = (CanonicalName) object;
    return this.full.equals(that.full);
  }

  @Override
  public final int hashCode() {
    return full.hashCode();
  }
}
