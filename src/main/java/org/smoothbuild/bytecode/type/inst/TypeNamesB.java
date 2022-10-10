package org.smoothbuild.bytecode.type.inst;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;

public class TypeNamesB {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static String arrayTypeName(TypeB elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(TypeB resT, List<? extends TypeB> paramTs) {
    return resT.name() + "(" + commaSeparatedTypeNames(paramTs) + ")";
  }

  public static String tupleTypeName(Iterable<? extends TypeB> itemTs) {
    return "{" + commaSeparatedTypeNames(itemTs) + "}";
  }

  private static String commaSeparatedTypeNames(Iterable<? extends TypeB> itemTs) {
    return toCommaSeparatedString(itemTs, TypeB::name);
  }
}
