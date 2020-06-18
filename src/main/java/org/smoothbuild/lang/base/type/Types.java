package org.smoothbuild.lang.base.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final Type MISSING = new MissingType();

  private static final BasicType BLOB = new BasicType("Blob");
  private static final BasicType BOOL = new BasicType("Bool");
  private static final BasicType NOTHING = new BasicType("Nothing");
  private static final BasicType STRING = new BasicType("String");

  public static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(
      BLOB, BOOL, NOTHING, STRING);

  public static Type missing() {
    return MISSING;
  }

  public static BasicGenericType generic(String name) {
    return new BasicGenericType(name);
  }

  public static BasicType blob() {
    return BLOB;
  }

  public static BasicType bool() {
    return BOOL;
  }

  public static BasicType nothing() {
    return NOTHING;
  }

  public static BasicType string() {
    return STRING;
  }

  public static StructType struct(String name, Iterable<Field> fields) {
    return new StructType(name, ImmutableList.copyOf(fields));
  }

  public static Type array(Type elemType) {
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
}
