package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class Types {

  public static final Type STRING = new StringType();
  public static final Type BLOB = new BlobType();
  public static final Type FILE = new FileType();
  public static final Type NOTHING = new NothingType();

  public static final ArrayType STRING_ARRAY = new ArrayType(STRING,
      new TypeLiteral<Array<SString>>() {});
  public static final ArrayType BLOB_ARRAY = new ArrayType(BLOB, new TypeLiteral<Array<Blob>>() {});
  public static final ArrayType FILE_ARRAY =
      new ArrayType(FILE, new TypeLiteral<Array<SFile>>() {});
  public static final ArrayType NIL = new ArrayType(NOTHING, new TypeLiteral<Array<Nothing>>() {});

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  /**
   * NOTHING is not a basic type as it is not possible to create instance of
   * that type.
   */
  private static final ImmutableSet<Type> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE);
  private static final ImmutableSet<ArrayType> ARRAY_TYPES = ImmutableSet.of(STRING_ARRAY,
      BLOB_ARRAY, FILE_ARRAY, NIL);

  private static final ImmutableSet<Type> RESULT_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  private static final ImmutableSet<Type> PARAMETER_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);
  private static final ImmutableSet<Type> ARRAY_ELEMENT_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING);
  private static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE, NOTHING,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);

  /*
   * Some of the set above converted to java types.
   */

  private static final ImmutableSet<TypeLiteral<?>> RESULT_JTYPES = toJTypes(RESULT_TYPES);
  private static final ImmutableSet<TypeLiteral<?>> PARAMETER_JTYPES = toJTypes(PARAMETER_TYPES);
  private static final ImmutableSet<TypeLiteral<?>> ARRAY_ELEMENT_JTYPES =
      toJTypes(ARRAY_ELEMENT_TYPES);

  /*
   * A few handy mappings.
   */

  private static final ImmutableMap<TypeLiteral<?>, Type> PARAMETER_JTYPE_TO_TYPE =
      createToTypeMap(PARAMETER_TYPES);
  private static final ImmutableMap<TypeLiteral<?>, Type> RESULT_JTYPE_TO_TYPE =
      createToTypeMap(RESULT_TYPES);
  private static final ImmutableMap<TypeLiteral<?>, Type> JTYPE_TO_TYPE =
      createToTypeMap(ALL_TYPES);
  private static final ImmutableMap<Type, ArrayType> ELEM_TYPE_TO_ARRAY_TYPE =
      createElemTypeToArrayTypeMap(ARRAY_TYPES);

  public static ImmutableSet<Type> basicTypes() {
    return BASIC_TYPES;
  }

  public static ImmutableSet<Type> parameterTypes() {
    return PARAMETER_TYPES;
  }

  /**
   * All smooth types available in smooth language. Returned list is sorted
   * using type - subtype relationship. Each type comes before all of its
   * subtypes.
   */
  public static ImmutableSet<Type> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> resultJTypes() {
    return RESULT_JTYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> parameterJTypes() {
    return PARAMETER_JTYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> arrayElementJTypes() {
    return ARRAY_ELEMENT_JTYPES;
  }

  public static Type parameterJTypeToType(TypeLiteral<?> jType) {
    return PARAMETER_JTYPE_TO_TYPE.get(jType);
  }

  public static Type resultJTypeToType(TypeLiteral<?> jType) {
    return RESULT_JTYPE_TO_TYPE.get(jType);
  }

  public static Type jTypeToType(TypeLiteral<?> jType) {
    return JTYPE_TO_TYPE.get(jType);
  }

  public static <T extends Value> ArrayType arrayTypeContaining(Type elemType) {
    return ELEM_TYPE_TO_ARRAY_TYPE.get(elemType);
  }

  private static ImmutableSet<TypeLiteral<?>> toJTypes(Iterable<Type> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();

    for (Type type : types) {
      builder.add(type.jType());
    }

    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, Type> createToTypeMap(Iterable<Type> types) {
    ImmutableMap.Builder<TypeLiteral<?>, Type> builder = ImmutableMap.builder();

    for (Type type : types) {
      builder.put(type.jType(), type);
    }

    return builder.build();
  }

  private static ImmutableMap<Type, ArrayType> createElemTypeToArrayTypeMap(
      ImmutableSet<ArrayType> arrayTypes) {

    ImmutableMap.Builder<Type, ArrayType> builder = ImmutableMap.builder();

    for (ArrayType type : arrayTypes) {
      builder.put(type.elemType(), type);
    }

    return builder.build();
  }

}
