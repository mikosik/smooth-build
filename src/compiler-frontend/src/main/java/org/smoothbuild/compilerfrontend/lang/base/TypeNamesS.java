package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class TypeNamesS {
  public static final String BLOB = "Blob";
  public static final String BOOL = "Bool";
  public static final String INT = "Int";
  public static final String STRING = "String";

  public static boolean isVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(TypeNamesS::isUpperCase);
  }

  public static boolean startsWithLowerCase(String name) {
    return !name.isEmpty() && isLowerCase(name.charAt(0));
  }

  public static boolean startsWithUpperCase(String name) {
    return !name.isEmpty() && isUpperCase(name.charAt(0));
  }

  public static boolean isLowerCase(int character) {
    return 'a' <= character && character <= 'z';
  }

  public static boolean isUpperCase(int character) {
    return 'A' <= character && character <= 'Z';
  }

  public static String arrayTypeName(SType elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(STupleType paramTs, SType resultT) {
    return "(" + commaSeparatedTypeNames(paramTs.elements()) + ")->" + resultT.name();
  }

  public static String tupleTypeName(List<? extends SType> elemTs) {
    return "(" + commaSeparatedTypeNames(elemTs) + ")";
  }

  public static String interfaceTypeName(Map<String, SItemSig> fields) {
    return listOfAll(fields.values()).toString("(", ",", ")");
  }

  private static String commaSeparatedTypeNames(List<? extends SType> elemTs) {
    return elemTs.map(SType::name).toString(",");
  }

  public static String fullName(String scopeName, String shortName) {
    if (scopeName == null) {
      return shortName;
    } else {
      return scopeName + ":" + shortName;
    }
  }
}
