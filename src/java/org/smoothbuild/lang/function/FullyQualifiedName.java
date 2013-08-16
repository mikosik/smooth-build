package org.smoothbuild.lang.function;

import java.util.regex.Pattern;

import com.google.common.base.Splitter;

public class FullyQualifiedName {
  private static final char SEPARATOR = '.';
  private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");

  private final String aPackage;
  private final String simple;
  private final String full;

  public static FullyQualifiedName simpleName(String simple) {
    if (!isValidSimpleName(simple)) {
      throw new IllegalArgumentException("Illegal function name: '" + simple + "'");
    }
    return new FullyQualifiedName("", simple, simple);
  }

  public static boolean isValidSimpleName(String simple) {
    return isValidName(simple);
  }

  public static FullyQualifiedName fullyQualifiedName(String fullyQualifiedName) {
    for (String part : Splitter.on(SEPARATOR).split(fullyQualifiedName)) {
      if (!isValidName(part)) {
        throw new IllegalArgumentException("Illegal fully qualified function name: '"
            + fullyQualifiedName + "'");
      }
    }
    int index = fullyQualifiedName.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return new FullyQualifiedName("", fullyQualifiedName, fullyQualifiedName);
    } else {
      String aPackage = fullyQualifiedName.substring(0, index);
      String name = fullyQualifiedName.substring(index + 1);
      return new FullyQualifiedName(aPackage, name, fullyQualifiedName);
    }
  }

  private static boolean isValidName(String name) {
    return NAME_PATTERN.matcher(name).matches();
  }

  private FullyQualifiedName(String aPackage, String simple, String full) {
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
    if (!(object instanceof FullyQualifiedName)) {
      return false;
    }
    FullyQualifiedName that = (FullyQualifiedName) object;
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
