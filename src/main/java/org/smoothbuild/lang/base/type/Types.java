package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Character.isUpperCase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Types {
  private static final AnyType ANY = new AnyType();
  private static final BlobType BLOB = new BlobType();
  private static final BoolType BOOL = new BoolType();
  private static final IntType INT = new IntType();
  private static final NothingType NOTHING = new NothingType();
  private static final StringType STRING = new StringType();

  /**
   * Base types that are legal in smooth language.
   */
  public static final ImmutableSet<BaseType> BASE_TYPES = ImmutableSet.of(
      BLOB,
      BOOL,
      INT,
      NOTHING,
      STRING
  );

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public static final ImmutableSet<BaseType> INFERABLE_BASE_TYPES =
      ImmutableSet.<BaseType>builder()
          .addAll(BASE_TYPES)
          .add(ANY)
          .build();

  public static Variable variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'", name);
    return new Variable(name);
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

  public static IntType int_() {
    return INT;
  }

  public static NothingType nothing() {
    return NOTHING;
  }

  public static StringType string() {
    return STRING;
  }

  public static StructType struct(String name, Iterable<ItemSignature> fields) {
    return new StructType(name, ImmutableList.copyOf(fields));
  }

  public static ArrayType array(Type elemType) {
    return new ArrayType(elemType);
  }

  public static FunctionType function(Type resultType, Iterable<ItemSignature> parameters) {
    return new FunctionType(resultType, ImmutableList.copyOf(parameters));
  }

  public static boolean isVariableName(String name) {
    return 1 == name.length() && isUpperCase(name.charAt(0));
  }
}
