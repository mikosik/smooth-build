package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.StringValue;

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
   * Smooth types. Used by smooth-build code to represent smooth types.
   */

  public static final Type STRING = new Type("String", STRING_T);
  public static final Type STRING_SET = new Type("String*", STRING_A_T);
  public static final Type BLOB = new Type("Blob", BLOB_T);
  public static final Type BLOB_SET = new Type("BLOB*", BLOB_A_T);
  public static final Type FILE = new Type("File", FILE_T);
  public static final Type FILE_SET = new Type("File*", FILE_A_T);
  public static final Type EMPTY_SET = new Type("Any*", EMPTY_A_T);

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

  private Type(String name, TypeLiteral<?> javaType) {
    this.name = name;
    this.javaType = javaType;
  }

  public String name() {
    return name;
  }

  public boolean isAssignableFrom(Type type) {
    if (type == EMPTY_SET) {
      return this == EMPTY_SET || this == STRING_SET || this == BLOB_SET || this == FILE_SET;
    } else {
      return this == type;
    }
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
