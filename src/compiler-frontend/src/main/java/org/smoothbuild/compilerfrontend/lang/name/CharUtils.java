package org.smoothbuild.compilerfrontend.lang.name;

public class CharUtils {
  static final char SEPARATOR = ':';

  static boolean isValidFullQualifiedNameCharacter(char c) {
    return isValidNameCharacter(c) || c == SEPARATOR;
  }

  static boolean isValidNameCharacter(char c) {
    // We allow '~' character internally even though it is forbidden by antlr grammar.
    // This way we can easily generate names for lambda that are unique and do not collide
    // with user defined names. It is also used to generate names for parameter default values.
    return isLowerCase(c) || isUpperCase(c) || isDigit(c) || c == '_' || c == '~';
  }

  static boolean isLowerCase(int character) {
    return 'a' <= character && character <= 'z';
  }

  static boolean isUpperCase(int character) {
    return 'A' <= character && character <= 'Z';
  }

  static boolean isDigit(int character) {
    return '0' <= character && character <= '9';
  }
}
