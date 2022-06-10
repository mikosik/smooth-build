package org.smoothbuild.lang.type;

import static java.lang.Character.isUpperCase;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;

public class TNamesS {
  public static final String ANY = "Any";
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String NOTHING = "Nothing";
  public static final String STRING = "String";

  public static boolean isVarName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }

  public static String arrayTypeName(MonoTS elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(MonoTS resT, List<? extends MonoTS> paramTs) {
    var params = toCommaSeparatedString(paramTs, MonoTS::name);
    return resT.name() + "(" + params + ")";
  }
}
