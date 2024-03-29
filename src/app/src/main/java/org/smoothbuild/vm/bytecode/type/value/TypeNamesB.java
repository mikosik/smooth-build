package org.smoothbuild.vm.bytecode.type.value;

import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;

import java.util.List;

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

  public static String tupleTypeName(Iterable<? extends TypeB> elementTypes) {
    return "{" + commaSeparatedTypeNames(elementTypes) + "}";
  }

  private static String commaSeparatedTypeNames(Iterable<? extends TypeB> types) {
    return joinWithCommaToString(types, TypeB::name);
  }
}
