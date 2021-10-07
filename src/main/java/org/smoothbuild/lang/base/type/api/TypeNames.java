package org.smoothbuild.lang.base.type.api;

import static java.lang.Character.isUpperCase;
import static java.util.stream.Collectors.joining;

import java.util.List;

public class TypeNames {
  public static final String ANY = "Any";
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String NOTHING = "Nothing";
  public static final String STRING = "String";

  public static boolean isVariableName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }

  public static String arrayTypeName(Type elemType) {
    return "[" + elemType.name() + "]";
  }

  public static String functionTypeName(Type resultType, List<? extends ItemSignature> parameters) {
    String parametersString = parameters
        .stream()
        .map(ItemSignature::toString)
        .collect(joining(", "));
    return resultType.name() + "(" + parametersString + ")";
  }
}
