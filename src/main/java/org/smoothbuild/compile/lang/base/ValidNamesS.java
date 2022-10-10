package org.smoothbuild.compile.lang.base;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;

import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeS;

public class ValidNamesS {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static boolean isVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(ValidNamesS::isUpperCase);
  }

  public static boolean isIdentifierName(String name) {
    return !(name.isEmpty() || name.equals("_"));
  }

  private static boolean isUpperCase(int character) {
    return 'A' <= character && character <= 'Z';
  }

  public static String arrayTypeName(TypeS elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(TypeS resT, TupleTS paramTs) {
    return resT.name() + "(" + commaSeparatedTypeNames(paramTs.items()) + ")";
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