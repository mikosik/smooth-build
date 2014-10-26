package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.SArrayType.sArrayType;
import static org.smoothbuild.lang.base.SType.sType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class STypes {

  public static final SType<SString> STRING = sType("String", SString.class);
  public static final SType<Blob> BLOB = sType("Blob", Blob.class);
  public static final SType<SFile> FILE = sType("File", SFile.class);
  public static final SType<SNothing> NOTHING = sType("Nothing", SNothing.class);

  public static final SArrayType<SString> STRING_ARRAY = sArrayType(STRING,
      new TypeLiteral<Array<SString>>() {});
  public static final SArrayType<Blob> BLOB_ARRAY = sArrayType(BLOB,
      new TypeLiteral<Array<Blob>>() {});
  public static final SArrayType<SFile> FILE_ARRAY = sArrayType(FILE,
      new TypeLiteral<Array<SFile>>() {});
  public static final SArrayType<SNothing> NIL = sArrayType(NOTHING,
      new TypeLiteral<Array<SNothing>>() {});

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  /**
   * NOTHING is not a basic type as it is not possible to create instance of
   * that type.
   */
  private static final ImmutableSet<SType<?>> BASIC_STYPES = ImmutableSet.of(STRING, BLOB, FILE);
  private static final ImmutableSet<SArrayType<?>> ARRAY_STYPES = ImmutableSet.of(STRING_ARRAY,
      BLOB_ARRAY, FILE_ARRAY, NIL);

  @SuppressWarnings("unchecked")
  private static final ImmutableSet<SType<?>> RESULT_STYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  @SuppressWarnings("unchecked")
  private static final ImmutableSet<SType<?>> PARAM_STYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);
  @SuppressWarnings("unchecked")
  private static final ImmutableSet<SType<?>> ALL_STYPES = ImmutableSet.of(STRING, BLOB, FILE,
      NOTHING, STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, NIL);

  /*
   * Some of the set above converted to java types.
   */

  private static final ImmutableSet<TypeLiteral<?>> RESULT_JTYPES = toJTypes(RESULT_STYPES);
  private static final ImmutableSet<TypeLiteral<?>> PARAM_JTYPES = toJTypes(PARAM_STYPES);

  /*
   * A few handy mappings.
   */

  private static final ImmutableMap<TypeLiteral<?>, SType<?>> PARAM_JTYPE_TO_STYPE = createToSTypeMap(
      PARAM_STYPES);
  private static final ImmutableMap<TypeLiteral<?>, SType<?>> RESULT_JTYPE_TO_STYPE = createToSTypeMap(
      RESULT_STYPES);
  private static final ImmutableMap<SType<?>, SArrayType<?>> ELEM_STYPE_TO_ARRAY_STYPE = createElemSTypeToSArrayTypeMap(
      ARRAY_STYPES);

  public static ImmutableSet<SType<?>> basicSTypes() {
    return BASIC_STYPES;
  }

  public static ImmutableSet<SType<?>> paramSTypes() {
    return PARAM_STYPES;
  }

  /**
   * All smooth types available in smooth language. Returned list is sorted
   * using type - subtype relationship. Each type comes before all of its
   * subtypes.
   */
  public static ImmutableSet<SType<?>> allSTypes() {
    return ALL_STYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> resultJTypes() {
    return RESULT_JTYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> paramJTypes() {
    return PARAM_JTYPES;
  }

  public static SType<?> paramJTypeToSType(TypeLiteral<?> jType) {
    return PARAM_JTYPE_TO_STYPE.get(jType);
  }

  public static SType<?> resultJTypeToSType(TypeLiteral<?> jType) {
    return RESULT_JTYPE_TO_STYPE.get(jType);
  }

  public static <T extends SValue> SArrayType<T> sArrayTypeContaining(SType<T> elemType) {
    /*
     * Cast is safe as ELEM_TYPE_TO_ARRAY_TYPE is immutable and it is
     * initialized with proper mappings.
     */
    @SuppressWarnings("unchecked")
    SArrayType<T> result = (SArrayType<T>) ELEM_STYPE_TO_ARRAY_STYPE.get(elemType);
    return result;
  }

  private static ImmutableSet<TypeLiteral<?>> toJTypes(Iterable<SType<?>> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();

    for (SType<?> type : types) {
      builder.add(type.jType());
    }

    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, SType<?>> createToSTypeMap(Iterable<SType<?>> types) {
    ImmutableMap.Builder<TypeLiteral<?>, SType<?>> builder = ImmutableMap.builder();

    for (SType<?> type : types) {
      builder.put(type.jType(), type);
    }

    return builder.build();
  }

  private static ImmutableMap<SType<?>, SArrayType<?>> createElemSTypeToSArrayTypeMap(
      ImmutableSet<SArrayType<?>> arrayTypes) {

    ImmutableMap.Builder<SType<?>, SArrayType<?>> builder = ImmutableMap.builder();

    for (SArrayType<?> type : arrayTypes) {
      builder.put(type.elemType(), type);
    }

    return builder.build();
  }

}
