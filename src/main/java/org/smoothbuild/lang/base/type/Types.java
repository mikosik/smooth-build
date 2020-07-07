package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Character.isUpperCase;

import org.smoothbuild.lang.base.type.compound.BasicCompoundability;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final Type MISSING = new MissingType();

  private static final ConcreteType BLOB = newBasicType("Blob");
  private static final ConcreteType BOOL = newBasicType("Bool");
  private static final ConcreteType NOTHING = newBasicType("Nothing");
  private static final ConcreteType STRING = newBasicType("String");
  private static final ConcreteType TYPE = newBasicType("Type");

  /**
   * Basic types available in smooth language. Note that `Type` doesn't belong to that list.
   * Smooth language doesn't have 'Type' type yet but it is declared already above and tested.
   */
  public static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(
      BLOB, BOOL, NOTHING, STRING);

  public static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.<Type>builder()
      .addAll(BASIC_TYPES)
      .add(TYPE)
      .add(MISSING)
      .build();

  private static ConcreteType newBasicType(String name) {
    return new ConcreteType(name, null, new BasicCompoundability());
  }

  public static Type missing() {
    return MISSING;
  }

  public static GenericType generic(String name) {
    checkArgument(isGenericTypeName(name), "Illegal generic type name '%s'", name);
    return new GenericType(name, new BasicCompoundability());
  }

  public static ConcreteType blob() {
    return BLOB;
  }

  public static ConcreteType bool() {
    return BOOL;
  }

  public static ConcreteType nothing() {
    return NOTHING;
  }

  public static ConcreteType string() {
    return STRING;
  }

  public static ConcreteType type() {
    return TYPE;
  }

  public static StructType struct(String name, Iterable<Field> fields) {
    return new StructType(name, ImmutableList.copyOf(fields));
  }

  public static Type array(Type elemType) {
    if (MISSING.equals(elemType)) {
      return MISSING;
    } else {
      if (elemType instanceof GenericType genericElemType) {
        return array(genericElemType);
      } else if (elemType instanceof ConcreteType concreteElemType) {
        return array(concreteElemType);
      } else {
        throw new RuntimeException("Unexpected class: " + elemType.getClass().getCanonicalName());
      }
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
