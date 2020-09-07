package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Character.isUpperCase;
import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.property.BasicProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final BlobType BLOB = new BlobType();
  private static final BoolType BOOL = new BoolType();
  private static final NothingType NOTHING = new NothingType();
  private static final StringType STRING = new StringType();

  /**
   * Basic types available in smooth language.
   */
  public static final ImmutableSet<ConcreteType> BASIC_TYPES = ImmutableSet.of(
      BLOB, BOOL, NOTHING, STRING);

  public static GenericType generic(String name) {
    checkArgument(isGenericTypeName(name), "Illegal generic type name '%s'", name);
    return new GenericType(name, internal(), new BasicProperties());
  }

  public static BlobType blob() {
    return BLOB;
  }

  public static BoolType bool() {
    return BOOL;
  }

  public static NothingType nothing() {
    return NOTHING;
  }

  public static StringType string() {
    return STRING;
  }

  public static StructType struct(String name, Location location, Iterable<Field> fields) {
    return new StructType(name, location, ImmutableList.copyOf(fields));
  }

  public static Type array(Type elemType) {
    if (elemType instanceof GenericType genericElemType) {
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
