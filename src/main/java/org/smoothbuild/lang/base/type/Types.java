package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Character.isUpperCase;

import org.smoothbuild.lang.base.Item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final AnyType ANY = new AnyType();
  private static final BlobType BLOB = new BlobType();
  private static final BoolType BOOL = new BoolType();
  private static final NothingType NOTHING = new NothingType();
  private static final StringType STRING = new StringType();

  /**
   * Base types available in smooth language.
   */
  public static final ImmutableSet<Type> BASE_TYPES = ImmutableSet.of(
      ANY,
      BLOB,
      BOOL,
      NOTHING,
      STRING
  );

  public static TypeVariable typeVariable(String name) {
    checkArgument(isTypeVariableName(name), "Illegal type variable name '%s'", name);
    return new TypeVariable(name);
  }

  public static AnyType any() {
    return ANY;
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

  public static StructType struct(String name, Iterable<Item> fields) {
    return new StructType(name, ImmutableList.copyOf(fields));
  }

  public static ArrayType array(Type elemType) {
    return new ArrayType(elemType);
  }

  public static boolean isTypeVariableName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }
}
