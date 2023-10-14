package org.smoothbuild.vm.bytecode.type.value;

import io.vavr.collection.Traversable;

public class TypeNamesB {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static String arrayTypeName(TypeB elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(Traversable<? extends TypeB> paramTs, TypeB resultT) {
    return "(" + commaSeparatedTypeNames(paramTs) + ")->" + resultT.name();
  }

  public static String tupleTypeName(Traversable<? extends TypeB> elementTypes) {
    return "{" + commaSeparatedTypeNames(elementTypes) + "}";
  }

  private static String commaSeparatedTypeNames(Traversable<? extends TypeB> types) {
    return types.mkString(",");
  }
}
