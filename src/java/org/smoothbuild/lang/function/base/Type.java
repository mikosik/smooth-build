package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.StringValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class Type {
  /*
   * Java types representing smooth types. These types are used by native
   * function implementations in plugins code.
   */

  private static final TypeLiteral<?> STRING_T = TypeLiteral.get(StringValue.class);
  private static final TypeLiteral<?> BLOB_T = TypeLiteral.get(Blob.class);
  private static final TypeLiteral<?> FILE_T = TypeLiteral.get(File.class);
  private static final TypeLiteral<?> STRING_A_T = new TypeLiteral<Array<StringValue>>() {};
  private static final TypeLiteral<?> BLOB_A_T = new TypeLiteral<Array<Blob>>() {};
  private static final TypeLiteral<?> FILE_A_T = new TypeLiteral<Array<File>>() {};
  private static final TypeLiteral<?> EMPTY_A_T = TypeLiteral.get(EmptySet.class);

  /*
   * Names of smooth types.
   */

  private static final String STRING_N = "String";
  private static final String BLOB_N = "Blob";
  private static final String FILE_N = "File";
  private static final String STRING_SET_N = "String*";
  private static final String BLOB_SET_N = "Blob*";
  private static final String FILE_SET_N = "File*";
  private static final String EMPTY_SET_N = "Any*";

  /*
   * Smooth types. Used by smooth-build code to represent smooth types.
   */

  public static final Type STRING = new Type(STRING_N, STRING_T);
  public static final Type BLOB = new Type(BLOB_N, BLOB_T);
  public static final Type FILE = new Type(FILE_N, FILE_T, BLOB);
  public static final Type STRING_SET = new Type(STRING_SET_N, STRING_A_T);
  public static final Type BLOB_SET = new Type(BLOB_SET_N, BLOB_A_T);
  public static final Type FILE_SET = new Type(FILE_SET_N, FILE_A_T, BLOB_SET);
  public static final Type EMPTY_SET = new Type(EMPTY_SET_N, EMPTY_A_T, STRING_SET, BLOB_SET,
      FILE_SET);

  /*
   * Not each type can be used in every place. Each set below represent one
   * place where smooth type can be used and contains all smooth types that can
   * be used there.
   */

  static final ImmutableSet<Type> SET_ELEM_TYPES = ImmutableSet.of(STRING, BLOB, FILE);
  static final ImmutableSet<Type> RESULT_TYPES = ImmutableSet.of(STRING, STRING_SET, BLOB,
      BLOB_SET, FILE, FILE_SET);
  static final ImmutableSet<Type> PARAM_TYPES = ImmutableSet.of(STRING, STRING_SET, BLOB, BLOB_SET,
      FILE, FILE_SET);
  static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, STRING_SET, BLOB, BLOB_SET,
      FILE, FILE_SET, EMPTY_SET);

  /*
   * Some of the sets above converted to java types.
   */

  static final ImmutableSet<TypeLiteral<?>> RESULT_JAVA_TYPES = toJavaTypes(RESULT_TYPES);
  static final ImmutableSet<TypeLiteral<?>> PARAM_JAVA_TYPES = toJavaTypes(PARAM_TYPES);

  /*
   * A few handy mappings.
   */

  static final ImmutableMap<TypeLiteral<?>, Type> JAVA_PARAM_TO_SMOOTH = javaToTypeMap(PARAM_TYPES);
  static final ImmutableMap<TypeLiteral<?>, Type> JAVA_RESULT_TO_SMOOTH = javaToTypeMap(RESULT_TYPES);

  /*
   * Instance fields.
   */

  private final String name;
  private final TypeLiteral<?> javaType;
  private final ImmutableList<Type> superTypes;

  private Type(String name, TypeLiteral<?> javaType, Type... superTypes) {
    this.name = name;
    this.javaType = javaType;
    this.superTypes = ImmutableList.copyOf(superTypes);
  }

  public String name() {
    return name;
  }

  public ImmutableList<Type> superTypes() {
    return superTypes;
  }

  public boolean isAssignableFrom(Type type) {
    if (this == type) {
      return true;
    }
    for (Type superType : type.superTypes) {
      if (this.isAssignableFrom(superType)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final boolean equals(Object object) {
    return this == object;
  }

  @Override
  public final int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return "'" + name + "'";
  }

  public static ImmutableSet<Type> allowedForSetElem() {
    return SET_ELEM_TYPES;
  }

  public static ImmutableSet<Type> allowedForParam() {
    return PARAM_TYPES;
  }

  public static ImmutableSet<Type> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> javaTypesAllowedForResult() {
    return RESULT_JAVA_TYPES;
  }

  public static ImmutableSet<TypeLiteral<?>> javaTypesAllowedForParam() {
    return PARAM_JAVA_TYPES;
  }

  public static Type javaParamTypetoType(TypeLiteral<?> javaType) {
    return JAVA_PARAM_TO_SMOOTH.get(javaType);
  }

  public static Type javaResultTypetoType(TypeLiteral<?> javaType) {
    return JAVA_RESULT_TO_SMOOTH.get(javaType);
  }

  private static ImmutableSet<TypeLiteral<?>> toJavaTypes(Iterable<Type> types) {
    ImmutableSet.Builder<TypeLiteral<?>> builder = ImmutableSet.builder();

    for (Type type : types) {
      builder.add(type.javaType);
    }

    return builder.build();
  }

  private static ImmutableMap<TypeLiteral<?>, Type> javaToTypeMap(Iterable<Type> types) {
    ImmutableMap.Builder<TypeLiteral<?>, Type> builder = ImmutableMap.builder();

    for (Type type : types) {
      builder.put(type.javaType, type);
    }

    return builder.build();
  }

  public static class EmptySet {
    private EmptySet() {}
  }
}
