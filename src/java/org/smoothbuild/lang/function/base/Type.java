package org.smoothbuild.lang.function.base;

import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.StringValue;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

public class Type {
  public static final Type STRING = new Type("String", StringValue.class);
  public static final Type STRING_SET = new Type("String*",
      new TypeLiteral<Array<StringValue>>() {});
  public static final Type BLOB = new Type("Blob", Blob.class);
  public static final Type BLOB_SET = new Type("BLOB*", new TypeLiteral<Array<Blob>>() {});
  public static final Type FILE = new Type("File", File.class);
  public static final Type FILE_SET = new Type("File*", new TypeLiteral<Array<File>>() {});
  public static final Type EMPTY_SET = new Type("Any*", EmptySet.class);

  static final ImmutableSet<Type> SET_ELEM_TYPES = ImmutableSet.of(STRING, BLOB, FILE);
  static final ImmutableSet<Type> RESULT_TYPES = ImmutableSet.of(STRING, STRING_SET, BLOB,
      BLOB_SET, FILE, FILE_SET);
  static final ImmutableSet<Type> PARAM_TYPES = ImmutableSet.of(STRING, STRING_SET, BLOB, BLOB_SET,
      FILE, FILE_SET);
  static final ImmutableSet<Type> ALL_TYPES = ImmutableSet.of(STRING, STRING_SET, BLOB, BLOB_SET,
      FILE, FILE_SET, EMPTY_SET);

  static final ImmutableSet<TypeLiteral<?>> RESULT_JAVA_TYPES = toJavaTypes(RESULT_TYPES);
  static final ImmutableSet<TypeLiteral<?>> PARAM_JAVA_TYPES = toJavaTypes(PARAM_TYPES);

  static final ImmutableMap<TypeLiteral<?>, Type> JAVA_PARAM_TO_SMOOTH = javaToTypeMap(PARAM_TYPES);
  static final ImmutableMap<TypeLiteral<?>, Type> JAVA_RESULT_TO_SMOOTH = javaToTypeMap(RESULT_TYPES);

  private final String name;
  private final TypeLiteral<?> javaType;

  private Type(String name, Class<?> javaType) {
    this(name, TypeLiteral.get(javaType));
  }

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
