package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class STypes {
  /*
   * Java types representing smooth types. These types are used by native
   * function implementations in plugins code.
   */

  private static final TypeLiteral<SString> STRING_T = TypeLiteral.get(SString.class);
  private static final TypeLiteral<SBlob> BLOB_T = TypeLiteral.get(SBlob.class);
  private static final TypeLiteral<SFile> FILE_T = TypeLiteral.get(SFile.class);
  private static final TypeLiteral<SNothing> NOTHING_T = TypeLiteral.get(SNothing.class);

  private static final TypeLiteral<SArray<SString>> STRING_A_T =
      new TypeLiteral<SArray<SString>>() {};
  private static final TypeLiteral<SArray<SBlob>> BLOB_A_T = new TypeLiteral<SArray<SBlob>>() {};
  private static final TypeLiteral<SArray<SFile>> FILE_A_T = new TypeLiteral<SArray<SFile>>() {};
  private static final TypeLiteral<SArray<SNothing>> EMPTY_A_T =
      new TypeLiteral<SArray<SNothing>>() {};

  /*
   * Names of smooth types.
   */

  private static final String STRING_N = "String";
  private static final String BLOB_N = "Blob";
  private static final String FILE_N = "File";
  private static final String NOTHING_N = "Nothing";

  /*
   * Smooth types. Used by smooth-build code to represent smooth types.
   */

  public static final SType<SString> STRING = new SType<SString>(STRING_N, STRING_T);
  public static final SType<SBlob> BLOB = new SType<SBlob>(BLOB_N, BLOB_T);
  public static final SType<SFile> FILE = new SType<SFile>(FILE_N, FILE_T);
  public static final SType<SNothing> NOTHING = new SType<SNothing>(NOTHING_N, NOTHING_T);

  public static final SArrayType<SString> STRING_ARRAY =
      new SArrayType<SString>(STRING_A_T, STRING);
  public static final SArrayType<SBlob> BLOB_ARRAY = new SArrayType<SBlob>(BLOB_A_T, BLOB);
  public static final SArrayType<SFile> FILE_ARRAY = new SArrayType<SFile>(FILE_A_T, FILE);
  public static final SArrayType<SNothing> EMPTY_ARRAY = new SArrayType<SNothing>(EMPTY_A_T,
      NOTHING);

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  static final ImmutableSet<SType<?>> BASIC_TYPES = ImmutableSet.of(STRING, BLOB, FILE);
  static final ImmutableSet<SArrayType<?>> ARRAY_TYPES = ImmutableSet.of(STRING_ARRAY, BLOB_ARRAY,
      FILE_ARRAY);

  @SuppressWarnings("unchecked")
  static final ImmutableSet<SType<?>> RESULT_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  @SuppressWarnings("unchecked")
  static final ImmutableSet<SType<?>> PARAM_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  @SuppressWarnings("unchecked")
  private static final ImmutableSet<SType<?>> ALL_TYPES = ImmutableSet.of(STRING, BLOB, FILE,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY, EMPTY_ARRAY);

  /*
   * Some of the set above converted to java types.
   */

  static final ImmutableSet<TypeLiteral<?>> RESULT_JAVA_TYPES = toJavaTypes(RESULT_TYPES);
  static final ImmutableSet<TypeLiteral<?>> PARAM_JAVA_TYPES = toJavaTypes(PARAM_TYPES);

  /*
   * A few handy mappings.
   */

  static final ImmutableMap<TypeLiteral<?>, SType<?>> JAVA_PARAM_TO_SMOOTH =
      javaToTypeMap(PARAM_TYPES);
  static final ImmutableMap<TypeLiteral<?>, SType<?>> JAVA_RESULT_TO_SMOOTH =
      javaToTypeMap(RESULT_TYPES);
  static final ImmutableMap<SType<?>, SArrayType<?>> ELEM_TYPE_TO_ARRAY_TYPE =
      createElemTypeToArrayType(ARRAY_TYPES);

  public static ImmutableSet<SType<?>> basicTypes() {
    return BASIC_TYPES;
  }

  public static ImmutableSet<SArrayType<?>> arrayTypes() {
    return ARRAY_TYPES;
  }

  public static ImmutableSet<SType<?>> allowedForParam() {
    return PARAM_TYPES;
  }

  /**
   * All smooth types available in smooth language. Returned list is sorted
   * using type - subtype relationship. Each type comes before all of its
   * subtypes.
   */
  public static ImmutableSet<SType<?>> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> javaTypesAllowedForResult() {
    return RESULT_JAVA_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> javaTypesAllowedForParam() {
    return PARAM_JAVA_TYPES;
  }

  public static SType<?> javaParamTypetoType(TypeLiteral<?> javaType) {
    return JAVA_PARAM_TO_SMOOTH.get(javaType);
  }

  public static SType<?> javaResultTypetoType(TypeLiteral<?> javaType) {
    return JAVA_RESULT_TO_SMOOTH.get(javaType);
  }

  public static <T extends SValue> SArrayType<T> arrayTypeContaining(SType<T> elemType) {
    /*
     * Cast is safe as ELEM_TYPE_TO_ARRAY_TYPE is immutable and it is
     * initialized with proper mappings.
     */
    @SuppressWarnings("unchecked")
    SArrayType<T> result = (SArrayType<T>) ELEM_TYPE_TO_ARRAY_TYPE.get(elemType);
    return result;
  }

  private static ImmutableSet<TypeLiteral<?>> toJavaTypes(Iterable<SType<?>> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();

    for (SType<?> type : types) {
      builder.add(type.javaType());
    }

    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, SType<?>> javaToTypeMap(Iterable<SType<?>> types) {
    ImmutableMap.Builder<TypeLiteral<?>, SType<?>> builder = ImmutableMap.builder();

    for (SType<?> type : types) {
      builder.put(type.javaType(), type);
    }

    return builder.build();
  }

  private static ImmutableMap<SType<?>, SArrayType<?>> createElemTypeToArrayType(
      ImmutableSet<SArrayType<?>> arrayTypes) {

    ImmutableMap.Builder<SType<?>, SArrayType<?>> builder = ImmutableMap.builder();

    for (SArrayType<?> type : arrayTypes) {
      builder.put(type.elemType(), type);
    }

    return builder.build();
  }
}
