package org.smoothbuild.lang.function;

import java.util.regex.Pattern;

public class Name {
  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_\\-0-9\\.]*");

  public static boolean isLegalName(String simple) {
    return PATTERN.matcher(simple).matches();
  }
}
