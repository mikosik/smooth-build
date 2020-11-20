package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Character.isUpperCase;

import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final BlobType BLOB = new BlobType();
  private static final BoolType BOOL = new BoolType();
  private static final NothingType NOTHING = new NothingType();
  private static final StringType STRING = new StringType();

  /**
   * Base types available in smooth language.
   */
  public static final ImmutableSet<Type> BASE_TYPES = ImmutableSet.of(BLOB, BOOL, NOTHING, STRING);

  public static TypeVariable typeVariable(String name) {
    checkArgument(isTypeVariableName(name), "Illegal type variable name '%s'", name);
    return new TypeVariable(name);
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

  public static StructType struct(String name, Location location, Iterable<Item> fields) {
    return new StructType(name, location, ImmutableList.copyOf(fields));
  }

  public static ArrayType array(Type elemType) {
    return new ArrayType(elemType);
  }

  public static boolean isTypeVariableName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }
}
