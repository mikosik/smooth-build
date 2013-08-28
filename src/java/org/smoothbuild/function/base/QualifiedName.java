package org.smoothbuild.function.base;

import java.util.regex.Pattern;

import com.google.common.base.Splitter;

public class QualifiedName {
  private static final char SEPARATOR = '.';
  private static final Pattern PART_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");

  private final String aPackage;
  private final String simple;
  private final String full;

  public static QualifiedName simpleName(String simple) {
    if (!isValidSimpleName(simple)) {
      throw new IllegalArgumentException("Illegal function name: '" + simple + "'");
    }
    return new QualifiedName("", simple, simple);
  }

  public static boolean isValidSimpleName(String simple) {
    return isValidPart(simple);
  }

  public static QualifiedName qualifiedName(String qualifiedName) {
    for (String part : Splitter.on(SEPARATOR).split(qualifiedName)) {
      if (!isValidPart(part)) {
        throw new IllegalArgumentException("Illegal qualified function name: '" + qualifiedName
            + "'");
      }
    }
    int index = qualifiedName.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return new QualifiedName("", qualifiedName, qualifiedName);
    } else {
      String aPackage = qualifiedName.substring(0, index);
      String name = qualifiedName.substring(index + 1);
      return new QualifiedName(aPackage, name, qualifiedName);
    }
  }

  private static boolean isValidPart(String part) {
    return PART_PATTERN.matcher(part).matches();
  }

  private QualifiedName(String aPackage, String simple, String full) {
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
    if (!(object instanceof QualifiedName)) {
      return false;
    }
    QualifiedName that = (QualifiedName) object;
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
