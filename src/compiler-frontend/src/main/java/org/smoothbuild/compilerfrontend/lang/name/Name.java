package org.smoothbuild.compilerfrontend.lang.name;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isUpperCase;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isValidNameCharacter;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Result;

public final class Name extends Id {
  public static Name typeName(String name) {
    var errorMessage = findTypeNameErrors(name);
    if (errorMessage != null) {
      throw new IllegalArgumentException(
          "Illegal type name " + Strings.q(name) + ". " + errorMessage);
    }
    return new Name(name);
  }

  public static Name referenceableName(String name) {
    var errorMessage = findReferenceableNameErrors(name);
    if (errorMessage != null) {
      throw new IllegalArgumentException(
          "Illegal referenceable name " + Strings.q(name) + ". " + errorMessage);
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

  public static Result<Name> parseTypeName(String name) {
    var errorMessage = findTypeNameErrors(name);
    if (errorMessage != null) {
      return err(errorMessage);
    }

    return ok(new Name(name));
  }

  public static String findTypeNameErrors(String name) {
    var errorMessage = findNameErrors(name);
    if (errorMessage != null) {
      return errorMessage;
    }
    if (!isUpperCase(name.charAt(0))) {
      return "It must start with uppercase letter.";
    }
    return null;
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
