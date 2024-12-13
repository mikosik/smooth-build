package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.Result.error;
import static org.smoothbuild.common.collect.Result.ok;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Result;

public class Id {
  private static final char SEPARATOR = ':';
  private static final String DOUBLE_SEPARATOR = "::";
  private final String full;
  private final String last;

  private Id(String full) {
    this.full = full;
    this.last = full.substring(full.lastIndexOf(SEPARATOR) + 1);
  }

  public String full() {
    return full;
  }

  public String last() {
    return last;
  }

  public Id append(Id id) {
    return new Id(full + SEPARATOR + id.full);
  }

  public static Id id(String name) {
    return parseReference(name).rightOrThrow(error -> {
      throw new IllegalArgumentException(error);
    });
  }

  public static Result<Id> parseIdentifierDeclaration(String name) {
    var error = parseShort(name);
    if (error != null) {
      return error;
    }
    if (!isLowerCase(name.charAt(0))) {
      return error("It must start with lowercase letter.");
    }

    return ok(new Id(name));
  }

  public static Result<Id> parseStructDeclaration(String name) {
    var error = parseShort(name);
    if (error != null) {
      return error;
    }
    if (!isUpperCase(name.charAt(0))) {
      return error("It must start with uppercase letter.");
    }
    if (isTypeVarName(name)) {
      return error("All-uppercase names are reserved for type variables.");
    }
    return ok(new Id(name));
  }

  public static Result<Id> parseReference(String name) {
    if (name.isEmpty()) {
      return error("It must not be empty string.");
    }
    boolean startOfPart = true;
    for (int i = 0; i < name.length(); i++) {
      var c = name.charAt(i);
      if (!isValidFullNameCharacter(c)) {
        return error("It must not contain '" + c + "' character.");
      }
      if (isDigit(c) && startOfPart) {
        return error("It must not contain part that starts with digit.");
      }
      startOfPart = c == SEPARATOR;
    }
    if (name.charAt(0) == SEPARATOR) {
      return error("It must not start with '" + SEPARATOR + "' character.");
    }
    if (name.charAt(name.length() - 1) == SEPARATOR) {
      return error("It must not end with '" + SEPARATOR + "' character.");
    }
    if (name.contains(DOUBLE_SEPARATOR)) {
      return error("It must not contain \"" + DOUBLE_SEPARATOR + "\" substring.");
    }

    return ok(new Id(name));
  }

  private static boolean isTypeVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(TokenNames::isUpperCase);
  }

  private static Result<Id> parseShort(String name) {
    if (name.isEmpty()) {
      return error("It must not be empty string.");
    }
    for (int i = 0; i < name.length(); i++) {
      var c = name.charAt(i);
      if (!isValidShortNameCharacter(c)) {
        return error("It must not contain '" + c + "' character.");
      }
    }
    if (name.equals("_")) {
      return error("`_` is reserved for future use.");
    }
    return null;
  }

  private static boolean isValidFullNameCharacter(char c) {
    return isValidShortNameCharacter(c) || c == SEPARATOR;
  }

  private static boolean isValidShortNameCharacter(char c) {
    return isLowerCase(c) || isUpperCase(c) || isDigit(c) || c == '_';
  }

  private static boolean isLowerCase(int character) {
    return 'a' <= character && character <= 'z';
  }

  private static boolean isUpperCase(int character) {
    return 'A' <= character && character <= 'Z';
  }

  private static boolean isDigit(int character) {
    return '0' <= character && character <= '9';
  }

  public String q() {
    return Strings.q(full);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Id id && Objects.equals(full, id.full);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(full);
  }

  @Override
  public String toString() {
    return full;
  }
}
