package org.smoothbuild.compilerfrontend.lang.name;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isUpperCase;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isValidNameCharacter;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Result;

public final class Name extends Id {
  public static Name structName(String name) {
    var errorMessage = findStructNameErrors(name);
    if (errorMessage != null) {
      throw new IllegalArgumentException("Illegal struct name. " + errorMessage);
    }
    return new Name(name);
  }

  public static Name referenceableName(String name) {
    var errorMessage = findReferenceableNameErrors(name);
    if (errorMessage != null) {
      throw new IllegalArgumentException("Illegal referenceable name. " + errorMessage);
    }
    return new Name(name);
  }

  public static Name typeVarName(String name) {
    var errorMessage = findTypeVarNameErrors(name);
    if (errorMessage != null) {
      throw new IllegalArgumentException("Illegal type var name. " + errorMessage);
    }
    return new Name(name);
  }

  Name(String name) {
    super(name);
  }

  public static Result<Name> parseReferenceableName(String name) {
    var errorMessage = findReferenceableNameErrors(name);
    if (errorMessage != null) {
      return err(errorMessage);
    }

    return ok(new Name(name));
  }

  private static String findReferenceableNameErrors(String name) {
    var errorMessage = findNameErrors(name);
    if (errorMessage != null) {
      return errorMessage;
    }
    if (!isLowerCase(name.charAt(0))) {
      return "It must start with lowercase letter.";
    }
    return null;
  }

  public static Result<Name> parseStructName(String name) {
    var errorMessage = findStructNameErrors(name);
    if (errorMessage != null) {
      return err(errorMessage);
    }

    return ok(new Name(name));
  }

  public static String findStructNameErrors(String name) {
    var errorMessage = findNameErrors(name);
    if (errorMessage != null) {
      return errorMessage;
    }
    if (!isUpperCase(name.charAt(0))) {
      return "It must start with uppercase letter.";
    }
    if (isTypeVarName(name)) {
      return "All-uppercase names are reserved for type variables.";
    }
    return null;
  }

  public static Result<Name> parseTypeVarName(String name) {
    var errorMessage = findTypeVarNameErrors(name);
    if (errorMessage != null) {
      return err(errorMessage);
    }

    return ok(new Name(name));
  }

  private static String findTypeVarNameErrors(String name) {
    var errorMessage = findNameErrors(name);
    if (errorMessage != null) {
      return errorMessage;
    }
    if (!isTypeVarName(name)) {
      return "Type variable must be UPPERCASE.";
    }
    return null;
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
      if (!isValidNameCharacter(c)) {
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
}
