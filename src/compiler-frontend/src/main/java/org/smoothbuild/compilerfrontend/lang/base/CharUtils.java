package org.smoothbuild.compilerfrontend.lang.base;

public class CharUtils {
  static final char SEPARATOR = ':';

  static boolean isValidFullQualifiedNameCharacter(char c) {
    return isValidNameCharacter(c) || c == SEPARATOR;
  }

  static boolean isValidNameCharacter(char c) {
    return isLowerCase(c) || isUpperCase(c) || isDigit(c) || c == '_';
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
