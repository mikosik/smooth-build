package org.smoothbuild.lang.object.type;

import static java.lang.Character.isUpperCase;

public class TypeNames {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String NOTHING = "Nothing";
  public static final String STRING = "String";
  public static final String TYPE = "Type";

  public static final String FILE = "File";
  public static final String MESSAGE = "Message";

  public static boolean isGenericTypeName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }
}
