package org.smoothbuild.compile.lang.type;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.lang.Character.isUpperCase;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;

public class TNamesS {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static boolean isVarName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }

  public static String arrayTypeName(TypeS elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(TypeS resT, List<? extends TypeS> paramTs) {
    return resT.name() + "(" + commaSeparatedTypeNames(paramTs) + ")";
  }

  public static String tupleTypeName(List<? extends TypeS> elemTs) {
    return "{" + commaSeparatedTypeNames(elemTs) + "}";
  }

  private static String commaSeparatedTypeNames(List<? extends TypeS> elemTs) {
    return toCommaSeparatedString(elemTs, TypeS::name);
  }

  public static String structNameToCtorName(String name) {
    return UPPER_CAMEL.to(LOWER_CAMEL, name);
  }
}
