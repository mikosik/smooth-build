package org.smoothbuild.compilerfrontend.lang.name;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.SEPARATOR;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isDigit;
import static org.smoothbuild.compilerfrontend.lang.name.CharUtils.isValidFullQualifiedNameCharacter;

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
    return parseReference(name).okOrThrow(error -> {
      throw new IllegalArgumentException("Illegal reference. " + error);
    });
  }

  public static Result<Fqn> parseReference(String string) {
    if (string.isEmpty()) {
      return err("It must not be empty string.");
    }
    boolean startOfPart = true;
    for (int i = 0; i < string.length(); i++) {
      var c = string.charAt(i);
      if (!isValidFullQualifiedNameCharacter(c)) {
        return err("It must not contain '" + c + "' character.");
      }
      if (isDigit(c) && startOfPart) {
        return err("It must not contain part that starts with digit.");
      }
      startOfPart = c == SEPARATOR;
    }
    if (string.charAt(0) == SEPARATOR) {
      return err("It must not start with '" + SEPARATOR + "' character.");
    }
    if (string.charAt(string.length() - 1) == SEPARATOR) {
      return err("It must not end with '" + SEPARATOR + "' character.");
    }
    if (string.contains(DOUBLE_SEPARATOR)) {
      return err("It must not contain \"" + DOUBLE_SEPARATOR + "\" substring.");
    }
    return ok(new Fqn(string));
  }

  @Override
  protected List<Name> splitToParts() {
    return listOfAll(
        Splitter.on(SEPARATOR).splitToStream(toString()).map(Name::new).toList());
  }
}
