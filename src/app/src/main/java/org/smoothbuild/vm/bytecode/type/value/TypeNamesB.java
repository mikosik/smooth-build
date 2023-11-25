package org.smoothbuild.vm.bytecode.type.value;

import org.smoothbuild.common.collect.List;

public class TypeNamesB {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static String arrayTypeName(TypeB elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(List<? extends TypeB> paramTs, TypeB resultT) {
    return "(" + commaSeparatedTypeNames(paramTs) + ")->" + resultT.name();
  }

  public static String tupleTypeName(List<? extends TypeB> elementTypes) {
    return "{" + commaSeparatedTypeNames(elementTypes) + "}";
  }

  private static String commaSeparatedTypeNames(List<? extends TypeB> types) {
    return types.toString(",");
  }
}
