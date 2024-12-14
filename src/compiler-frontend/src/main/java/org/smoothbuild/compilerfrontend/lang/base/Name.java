package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.error;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isUpperCase;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isValidShortNameCharacter;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Result;

public final class Name extends Id {
  Name(String name) {
    super(name);
  }

  public static Result<Name> parseReferenceableName(String name) {
    var errorMessage = findNameErrors(name);
    if (errorMessage != null) {
      return error(errorMessage);
    }
    if (!isLowerCase(name.charAt(0))) {
      return error("It must start with lowercase letter.");
    }

    return ok(new Name(name));
  }

  public static Result<Name> parseStructName(String name) {
    var errorMessage = findNameErrors(name);
    if (errorMessage != null) {
      return error(errorMessage);
    }
    if (!isUpperCase(name.charAt(0))) {
      return error("It must start with uppercase letter.");
    }
    if (isTypeVarName(name)) {
      return error("All-uppercase names are reserved for type variables.");
    }
    return ok(new Name(name));
  }

  private static boolean isTypeVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(CharUtils::isUpperCase);
  }

  private static String findNameErrors(String name) {
    if (name.isEmpty()) {
      return "It must not be empty string.";
    }
    for (int i = 0; i < name.length(); i++) {
      var c = name.charAt(i);
      if (!isValidShortNameCharacter(c)) {
        return "It must not contain '" + c + "' character.";
      }
    }
    if (name.equals("_")) {
      return "`_` is reserved for future use.";
    }
    return null;
  }

  @Override
  protected List<Name> splitToParts() {
    return list(this);
  }

  @Override
  public String last() {
    return full();
  }
}
