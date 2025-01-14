package org.smoothbuild.virtualmachine.bytecode.kind.base;

import org.smoothbuild.common.collect.List;

public class BTypeNames {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static String arrayTypeName(BType elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String lambdaTypeName(List<? extends BType> paramTs, BType resultT) {
    return "(" + commaSeparatedTypeNames(paramTs) + ")->" + resultT.name();
  }

  public static String tupleTypeName(List<? extends BType> elementTypes) {
    return "{" + commaSeparatedTypeNames(elementTypes) + "}";
  }

  public static String choiceTypeName(List<? extends BType> elementTypes) {
    return "{" + elementTypes.toString("|") + "}";
  }

  private static String commaSeparatedTypeNames(List<? extends BType> types) {
    return types.toString(",");
  }
}
