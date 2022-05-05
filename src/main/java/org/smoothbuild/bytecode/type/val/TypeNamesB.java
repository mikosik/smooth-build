package org.smoothbuild.bytecode.type.val;

import static java.lang.Character.isUpperCase;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;

import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.VarSet;

public class TypeNamesB {
  public static final String ANY = "Any";
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String NOTHING = "Nothing";
  public static final String STRING = "String";

  public static boolean isVarName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }

  public static String arrayTypeName(Type elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(
      VarSet<? extends Type> tParams, Type resT, List<? extends Type> paramTs) {
    var params = toCommaSeparatedString(paramTs, Type::name);
    return tParams + resT.name() + "(" + params + ")";
  }

  public static String tupleTypeName(Iterable<? extends Type> itemTs) {
    return "{" + toCommaSeparatedString(itemTs, Type::name) + "}";
  }
}
