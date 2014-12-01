package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.ArrayType.arrayType;
import static org.smoothbuild.lang.base.Type.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class Types {

  public static final Type<SString> STRING = type("String", SString.class);
  public static final Type<Blob> BLOB = type("Blob", Blob.class);
  public static final Type<SFile> FILE = type("File", SFile.class);
  public static final Type<Nothing> NOTHING = type("Nothing", Nothing.class);

  public static final ArrayType<SString> STRING_ARRAY = arrayType(STRING,
      new TypeLiteral<Array<SString>>() {});
  public static final ArrayType<Blob> BLOB_ARRAY = arrayType(BLOB,
      new TypeLiteral<Array<Blob>>() {});
  public static final ArrayType<SFile> FILE_ARRAY = arrayType(FILE,
      new TypeLiteral<Array<SFile>>() {});
  public static final ArrayType<Nothing> NIL = arrayType(NOTHING,
      new TypeLiteral<Array<Nothing>>() {});

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  /**
   * NOTHING is not a basic type as it is not possible to create instance of
   * that type.
   */
  private static final ImmutableSet<Type<?>> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE);
  private static final ImmutableSet<ArrayType<?>> ARRAY_TYPES = ImmutableSet.of(STRING_ARRAY,
      BLOB_ARRAY, FILE_ARRAY, NIL);

  @SuppressWarnings("unchecked")
  private static final ImmutableSet<Type<?>> RESULT_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  @SuppressWarnings("unchecked")
  private static final ImmutableSet<Type<?>> PARAMETER_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);
  @SuppressWarnings("unchecked")
  private static final ImmutableSet<Type<?>> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING, STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);

  /*
   * Some of the set above converted to java types.
   */

  private static final ImmutableSet<TypeLiteral<?>> RESULT_JTYPES = toJTypes(RESULT_TYPES);
  private static final ImmutableSet<TypeLiteral<?>> PARAMETER_JTYPES = toJTypes(PARAMETER_TYPES);

  /*
   * A few handy mappings.
   */

  private static final ImmutableMap<TypeLiteral<?>, Type<?>> PARAMETER_JTYPE_TO_TYPE =
      createToTypeMap(PARAMETER_TYPES);
  private static final ImmutableMap<TypeLiteral<?>, Type<?>> RESULT_JTYPE_TO_TYPE =
      createToTypeMap(RESULT_TYPES);
  private static final ImmutableMap<Type<?>, ArrayType<?>> ELEM_TYPE_TO_ARRAY_TYPE =
      createElemTypeToArrayTypeMap(ARRAY_TYPES);

  public static ImmutableSet<Type<?>> basicTypes() {
    return BASIC_TYPES;
  }

  public static ImmutableSet<Type<?>> parameterTypes() {
    return PARAMETER_TYPES;
  }

  /**
   * All smooth types available in smooth language. Returned list is sorted
   * using type - subtype relationship. Each type comes before all of its
   * subtypes.
   */
  public static ImmutableSet<Type<?>> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> resultJTypes() {
    return RESULT_JTYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> parameterJTypes() {
    return PARAMETER_JTYPES;
  }

  public static Type<?> parameterJTypeToType(TypeLiteral<?> jType) {
    return PARAMETER_JTYPE_TO_TYPE.get(jType);
  }

  public static Type<?> resultJTypeToType(TypeLiteral<?> jType) {
    return RESULT_JTYPE_TO_TYPE.get(jType);
  }

  public static <T extends Value> ArrayType<T> arrayTypeContaining(Type<T> elemType) {
    /*
     * Cast is safe as ELEM_TYPE_TO_ARRAY_TYPE is immutable and it is
     * initialized with proper mappings.
     */
    @SuppressWarnings("unchecked")
    ArrayType<T> result = (ArrayType<T>) ELEM_TYPE_TO_ARRAY_TYPE.get(elemType);
    return result;
  }

  private static ImmutableSet<TypeLiteral<?>> toJTypes(Iterable<Type<?>> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();

    for (Type<?> type : types) {
      builder.add(type.jType());
    }

    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, Type<?>> createToTypeMap(Iterable<Type<?>> types) {
    ImmutableMap.Builder<TypeLiteral<?>, Type<?>> builder = ImmutableMap.builder();

    for (Type<?> type : types) {
      builder.put(type.jType(), type);
    }

    return builder.build();
  }

  private static ImmutableMap<Type<?>, ArrayType<?>> createElemTypeToArrayTypeMap(
      ImmutableSet<ArrayType<?>> arrayTypes) {

    ImmutableMap.Builder<Type<?>, ArrayType<?>> builder = ImmutableMap.builder();

    for (ArrayType<?> type : arrayTypes) {
      builder.put(type.elemType(), type);
    }

    return builder.build();
  }

}
