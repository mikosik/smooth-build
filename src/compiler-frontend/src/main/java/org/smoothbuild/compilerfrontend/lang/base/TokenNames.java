package org.smoothbuild.compilerfrontend.lang.base;

import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class TokenNames {
  public static boolean isTypeVarName(String name) {
    return !name.isEmpty() && name.chars().allMatch(TokenNames::isUpperCase);
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

  public static String funcTypeName(STupleType paramTypes, SType resultType) {
    return "(" + commaSeparatedTypeNames(paramTypes.elements()) + ")->" + resultType.name();
  }

  public static String tupleTypeName(List<? extends SType> elemTypes) {
    return "{" + commaSeparatedTypeNames(elemTypes) + "}";
  }

  public static String interfaceTypeName(Map<Id, SItemSig> fields) {
    return listOfAll(fields.values()).toString("{", ",", "}");
  }

  private static String commaSeparatedTypeNames(List<? extends SType> elemTypes) {
    return elemTypes.map(SType::name).toString(",");
  }
}
