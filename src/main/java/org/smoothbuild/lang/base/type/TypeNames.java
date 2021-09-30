package org.smoothbuild.lang.base.type;

import static java.lang.Character.isUpperCase;

public class TypeNames {
  public static final String ANY = "Any";
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String NOTHING = "Nothing";
  public static final String STRING = "String";

  public static boolean isVariableName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }
}
