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
    var params = toCommaSeparatedString(paramTs, TypeB::name);
    return resT.name() + "(" + params + ")";
  }

  public static String tupleTypeName(Iterable<? extends TypeB> itemTs) {
    return "{" + toCommaSeparatedString(itemTs, TypeB::name) + "}";
  }
}
