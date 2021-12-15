package org.smoothbuild.lang.base.type.api;

import static java.lang.Character.isUpperCase;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;

public class TypeNames {
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

  public static String funcTypeName(Type resT, List<? extends Type> paramTs) {
    String paramsString = paramTs
        .stream()
        .map(Type::name)
        .collect(joining(", "));
    return resT.name() + "(" + paramsString + ")";
  }

  public static String tupleTypeName(Iterable<? extends Type> itemTs) {
    return "{" + toCommaSeparatedString(itemTs, Type::name) + "}";
  }
}
