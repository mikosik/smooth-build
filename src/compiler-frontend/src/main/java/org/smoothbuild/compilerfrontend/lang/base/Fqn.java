package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Result.error;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.SEPARATOR;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isDigit;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isValidFullNameCharacter;

import com.google.common.base.Splitter;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Result;

/**
 * Fully qualified name.
 * This class is immutable.
 */
public final class Fqn extends Id {
  private static final String DOUBLE_SEPARATOR = "::";

  Fqn(String string) {
    super(string);
  }

  public static Fqn fqn(String name) {
    return parseReference(name).rightOrThrow(error -> {
      throw new IllegalArgumentException(error);
    });
  }

  public static Result<Fqn> parseReference(String string) {
    if (string.isEmpty()) {
      return error("It must not be empty string.");
    }
    boolean startOfPart = true;
    for (int i = 0; i < string.length(); i++) {
      var c = string.charAt(i);
      if (!isValidFullNameCharacter(c)) {
        return error("It must not contain '" + c + "' character.");
      }
      if (isDigit(c) && startOfPart) {
        return error("It must not contain part that starts with digit.");
      }
      startOfPart = c == SEPARATOR;
    }
    if (string.charAt(0) == SEPARATOR) {
      return error("It must not start with '" + SEPARATOR + "' character.");
    }
    if (string.charAt(string.length() - 1) == SEPARATOR) {
      return error("It must not end with '" + SEPARATOR + "' character.");
    }
    if (string.contains(DOUBLE_SEPARATOR)) {
      return error("It must not contain \"" + DOUBLE_SEPARATOR + "\" substring.");
    }
    return ok(new Fqn(string));
  }

  @Override
  protected List<Name> splitToParts() {
    return listOfAll(
        Splitter.on(SEPARATOR).splitToStream(toString()).map(Name::new).toList());
  }
}
