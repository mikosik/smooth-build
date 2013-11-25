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
  private static final TypeLiteral<SArray<SString>> STRING_A_T = new TypeLiteral<SArray<SString>>() {};
  private static final TypeLiteral<SArray<SBlob>> BLOB_A_T = new TypeLiteral<SArray<SBlob>>() {};
  private static final TypeLiteral<SArray<SFile>> FILE_A_T = new TypeLiteral<SArray<SFile>>() {};
  private static final TypeLiteral<SArray<Any>> EMPTY_A_T = new TypeLiteral<SArray<Any>>() {};

  /*
   * Names of smooth types.
   */

  private static final String STRING_N = "String";
  private static final String BLOB_N = "Blob";
  private static final String FILE_N = "File";
  private static final String STRING_A_N = "String*";
  private static final String BLOB_A_N = "Blob*";
  private static final String FILE_A_N = "File*";
  private static final String EMPTY_A_N = "Any*";

  /*
   * Smooth types. Used by smooth-build code to represent smooth types.
   */

  public static final Type<SString> STRING = new Type<SString>(STRING_N, STRING_T);
  public static final Type<SBlob> BLOB = new Type<SBlob>(BLOB_N, BLOB_T);
  public static final Type<SFile> FILE = new Type<SFile>(FILE_N, FILE_T, BLOB);

  public static final ArrayType<SString> STRING_ARRAY = new ArrayType<SString>(STRING_A_N,
      STRING_A_T);
  public static final ArrayType<SBlob> BLOB_ARRAY = new ArrayType<SBlob>(BLOB_A_N, BLOB_A_T);
  public static final ArrayType<SFile> FILE_ARRAY = new ArrayType<SFile>(FILE_A_N, FILE_A_T,
      BLOB_ARRAY);
  public static final ArrayType<Any> EMPTY_ARRAY = new ArrayType<Any>(EMPTY_A_N, EMPTY_A_T,
      STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  static final ImmutableSet<Type<?>> ARRAY_ELEM_TYPES = ImmutableSet.of(STypes.STRING, STypes.BLOB,
      STypes.FILE);
  @SuppressWarnings("unchecked")
  static final ImmutableSet<Type<?>> RESULT_TYPES = ImmutableSet.of(STypes.STRING,
      STypes.STRING_ARRAY, STypes.BLOB, STypes.BLOB_ARRAY, STypes.FILE, STypes.FILE_ARRAY);
  @SuppressWarnings("unchecked")
  static final ImmutableSet<Type<?>> PARAM_TYPES = ImmutableSet.of(STypes.STRING,
      STypes.STRING_ARRAY, STypes.BLOB, STypes.BLOB_ARRAY, STypes.FILE, STypes.FILE_ARRAY);
  @SuppressWarnings("unchecked")
  static final ImmutableSet<Type<?>> ALL_TYPES = ImmutableSet.of(STypes.STRING,
      STypes.STRING_ARRAY, STypes.BLOB, STypes.BLOB_ARRAY, STypes.FILE, STypes.FILE_ARRAY,
      STypes.EMPTY_ARRAY);

  /*
   * Some of the set above converted to java types.
   */

  static final ImmutableSet<TypeLiteral<?>> RESULT_JAVA_TYPES = toJavaTypes(RESULT_TYPES);
  static final ImmutableSet<TypeLiteral<?>> PARAM_JAVA_TYPES = toJavaTypes(PARAM_TYPES);

  /*
   * A few handy mappings.
   */

  static final ImmutableMap<TypeLiteral<?>, Type<?>> JAVA_PARAM_TO_SMOOTH = javaToTypeMap(PARAM_TYPES);
  static final ImmutableMap<TypeLiteral<?>, Type<?>> JAVA_RESULT_TO_SMOOTH = javaToTypeMap(RESULT_TYPES);

  public static ImmutableSet<Type<?>> allowedForArrayElem() {
    return ARRAY_ELEM_TYPES;
  }

  public static ImmutableSet<Type<?>> allowedForParam() {
    return PARAM_TYPES;
  }

  public static ImmutableSet<Type<?>> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> javaTypesAllowedForResult() {
    return RESULT_JAVA_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> javaTypesAllowedForParam() {
    return PARAM_JAVA_TYPES;
  }

  public static Type<?> javaParamTypetoType(TypeLiteral<?> javaType) {
    return JAVA_PARAM_TO_SMOOTH.get(javaType);
  }

  public static Type<?> javaResultTypetoType(TypeLiteral<?> javaType) {
    return JAVA_RESULT_TO_SMOOTH.get(javaType);
  }

  private static ImmutableSet<TypeLiteral<?>> toJavaTypes(Iterable<Type<?>> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();

    for (Type<?> type : types) {
      builder.add(type.javaType());
    }

    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, Type<?>> javaToTypeMap(Iterable<Type<?>> types) {
    ImmutableMap.Builder<TypeLiteral<?>, Type<?>> builder = ImmutableMap.builder();

    for (Type<?> type : types) {
      builder.put(type.javaType(), type);
    }

    return builder.build();
  }

  private static interface Any extends Value {}
}
