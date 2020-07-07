package org.smoothbuild.lang.base.type;

import static java.lang.Character.isUpperCase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final IType MISSING = new MissingType();

  private static final BasicConcreteType BLOB = new BasicConcreteType("Blob");
  private static final BasicConcreteType BOOL = new BasicConcreteType("Bool");
  private static final BasicConcreteType NOTHING = new BasicConcreteType("Nothing");
  private static final BasicConcreteType STRING = new BasicConcreteType("String");
  private static final BasicConcreteType TYPE = new BasicConcreteType("Type");

  /**
   * Basic types available in smooth language. Note that `Type` doesn't belong to that list.
   * Smooth language doesn't have 'Type' type yet but it is declared already above and tested.
   */
  public static final ImmutableSet<IType> BASIC_TYPES = ImmutableSet.of(
      BLOB, BOOL, NOTHING, STRING);

  public static final ImmutableSet<IType> ALL_TYPES = ImmutableSet.<IType>builder()
      .addAll(BASIC_TYPES)
      .add(TYPE)
      .add(MISSING)
      .build();

  public static IType missing() {
    return MISSING;
  }

  public static BasicGenericType generic(String name) {
    return new BasicGenericType(name);
  }

  public static BasicConcreteType blob() {
    return BLOB;
  }

  public static BasicConcreteType bool() {
    return BOOL;
  }

  public static BasicConcreteType nothing() {
    return NOTHING;
  }

  public static BasicConcreteType string() {
    return STRING;
  }

  public static BasicConcreteType type() {
    return TYPE;
  }

  public static StructType struct(String name, Iterable<Field> fields) {
    return new StructType(name, ImmutableList.copyOf(fields));
  }

  public static IType array(IType elemType) {
    if (MISSING.equals(elemType)) {
      return MISSING;
    } else if (elemType instanceof GenericType genericElemType) {
      return array(genericElemType);
    } else if (elemType instanceof ConcreteType concreteElemType) {
      return array(concreteElemType);
    } else {
      throw new RuntimeException("Unexpected class: " + elemType.getClass().getCanonicalName());
    }
  }

  public static ConcreteArrayType array(ConcreteType elemType) {
    return new ConcreteArrayType(elemType);
  }

  public static GenericArrayType array(GenericType elemType) {
    return new GenericArrayType(elemType);
  }

  public static boolean isGenericTypeName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }
}
