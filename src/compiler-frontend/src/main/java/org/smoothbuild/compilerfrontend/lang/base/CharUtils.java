package org.smoothbuild.compilerfrontend.lang.base;

public class CharUtils {
  static final char SEPARATOR = ':';
  static final String SEPARATOR_STRING = ":";

  static boolean isValidFullNameCharacter(char c) {
    return isValidShortNameCharacter(c) || c == SEPARATOR;
  }

  static boolean isValidShortNameCharacter(char c) {
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
