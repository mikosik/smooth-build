package org.smoothbuild.compile.fs.lang.base;

import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;

import java.util.List;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

import com.google.common.collect.ImmutableMap;

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

  public static String arrayTypeName(TypeS elemT) {
    return "[" + elemT.name() + "]";
  }

  public static String funcTypeName(TupleTS paramTs, TypeS resultT) {
    return "(" + commaSeparatedTypeNames(paramTs.elements()) + ")->" + resultT.name();
  }

  public static String tupleTypeName(List<? extends TypeS> elemTs) {
    return "(" + commaSeparatedTypeNames(elemTs) + ")";
  }

  public static String interfaceTypeName(ImmutableMap<String, ItemSigS> fields) {
    return "(" + joinWithCommaToString(fields.values()) + ")";
  }

  private static String commaSeparatedTypeNames(List<? extends TypeS> elemTs) {
    return joinWithCommaToString(elemTs, TypeS::name);
  }
}
