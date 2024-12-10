package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class TokenNames {
  public static boolean isTypeVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(TokenNames::isUpperCase);
  }

  public static Maybe<String> detectIdentifierNameErrors(String name) {
    if (name.isEmpty()) {
      return some("It must not be empty string.");
    }
    if (name.equals("_")) {
      return some("`_` is reserved for future use.");
    }
    if (!isLowerCase(name.charAt(0))) {
      return some("It must start with lowercase letter.");
    }
    for (int i = 0; i < name.length(); i++) {
      var c = name.charAt(i);
      if (!isValidIdentifierNameCharacter(c)) {
        return some("It must not contain '" + c + "' character.");
      }
    }
    return none();
  }

  private static boolean isValidIdentifierNameCharacter(char c) {
    return isLowerCase(c) || isUpperCase(c) || isDigit(c) || c == '_';
  }

  public static Maybe<String> detectStructNameErrors(String name) {
    if (name.isEmpty()) {
      return some("It must not be empty string.");
    }
    if (name.equals("_")) {
      return some("`_` is reserved for future use.");
    }
    if (!isUpperCase(name.charAt(0))) {
      return some("It must start with uppercase letter.");
    }
    if (isTypeVarName(name)) {
      return some("All-uppercase names are reserved for type variables.");
    }
    for (int i = 0; i < name.length(); i++) {
      var c = name.charAt(i);
      if (!isValidIdentifierNameCharacter(c)) {
        return some("It must not contain '" + c + "' character.");
      }
    }
    return none();
  }

  public static boolean isLowerCase(int character) {
    return 'a' <= character && character <= 'z';
  }

  public static boolean isUpperCase(int character) {
    return 'A' <= character && character <= 'Z';
  }

  public static boolean isDigit(int character) {
    return '0' <= character && character <= '9';
  }

  public static String arrayTypeName(SType elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(STupleType paramTypes, SType resultType) {
    return "(" + commaSeparatedTypeNames(paramTypes.elements()) + ")->" + resultType.name();
  }

  public static String tupleTypeName(List<? extends SType> elemTypes) {
    return "{" + commaSeparatedTypeNames(elemTypes) + "}";
  }

  public static String interfaceTypeName(Map<String, SItemSig> fields) {
    return listOfAll(fields.values()).toString("{", ",", "}");
  }

  private static String commaSeparatedTypeNames(List<? extends SType> elemTypes) {
    return elemTypes.map(SType::name).toString(",");
  }

  public static String fullName(String ownerName, String shortName) {
    return ownerName + ":" + shortName;
  }
}
