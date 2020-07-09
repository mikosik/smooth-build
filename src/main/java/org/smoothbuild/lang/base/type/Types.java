package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Character.isUpperCase;
import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.compound.BasicCompoundability;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final Type MISSING = new MissingType();

  private static final ConcreteType BLOB = new BlobType();
  private static final ConcreteType BOOL = new BoolType();
  private static final ConcreteType NOTHING = new NothingType();
  private static final ConcreteType STRING = new StringType();
  private static final ConcreteType TYPE = new TypeType();

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

  public static Type missing() {
    return MISSING;
  }

  public static GenericType generic(String name) {
    checkArgument(isGenericTypeName(name), "Illegal generic type name '%s'", name);
    return new GenericType(name, internal(), new BasicCompoundability(SObject.class));
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

  public static StructType struct(String name, Location location, Iterable<Field> fields) {
    return new StructType(name, location, ImmutableList.copyOf(fields));
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
