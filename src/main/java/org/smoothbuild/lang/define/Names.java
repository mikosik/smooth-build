package org.smoothbuild.lang.define;

import java.util.regex.Pattern;

public class Names {
  private static final Pattern PATTERN = Pattern.compile("[a-z][a-zA-Z0-9_]*");

  public static boolean isLegalName(String simple) {
    return PATTERN.matcher(simple).matches();
  }
}
