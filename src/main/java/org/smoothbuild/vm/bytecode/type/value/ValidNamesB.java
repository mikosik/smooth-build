package org.smoothbuild.vm.bytecode.type.value;

import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;

import java.util.List;

import org.smoothbuild.util.collect.Iterables;

public class ValidNamesB {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static String arrayTypeName(TypeB elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(List<? extends TypeB> paramTs, TypeB resT) {
    return "(" + commaSeparatedTypeNames(paramTs) + ")->" + resT.name();
  }

  public static String tupleTypeName(Iterable<? extends TypeB> itemTs) {
    return "{" + commaSeparatedTypeNames(itemTs) + "}";
  }

  private static String commaSeparatedTypeNames(Iterable<? extends TypeB> itemTs) {
    return joinWithCommaToString(itemTs, TypeB::name);
  }
}
