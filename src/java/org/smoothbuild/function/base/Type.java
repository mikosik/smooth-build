package org.smoothbuild.function.base;

import org.smoothbuild.fs.plugin.EmptySet;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.StringSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

public class Type {
  public static final Type STRING = create("String", String.class);
  public static final Type STRING_SET = create("StringSet", StringSet.class);
  public static final Type FILE = create("File", File.class);
  public static final Type FILE_SET = create("FileSet", FileSet.class);
  public static final Type VOID = create("Void", Void.TYPE);
  public static final Type EMPTY_SET = create("EmptySet", EmptySet.class);

  static final ImmutableList<Type> SET_ELEM_TYPES = ImmutableList.of(STRING, FILE);
  static final ImmutableList<Type> RESULT_TYPES = ImmutableList.of(STRING, STRING_SET, FILE,
      FILE_SET, VOID);
  static final ImmutableList<Type> PARAM_TYPES = ImmutableList.of(STRING, STRING_SET, FILE,
      FILE_SET);

  static final ImmutableList<TypeLiteral<?>> RESULT_JAVA_TYPES = toJavaTypes(RESULT_TYPES);
  static final ImmutableList<TypeLiteral<?>> PARAM_JAVA_TYPES = toJavaTypes(PARAM_TYPES);

  static final ImmutableMap<TypeLiteral<?>, Type> JAVA_PARAM_TO_SMOOTH = javaToTypeMap(PARAM_TYPES);
  static final ImmutableMap<TypeLiteral<?>, Type> JAVA_RESULT_TO_SMOOTH = javaToTypeMap(RESULT_TYPES);

  private final String name;
  private final TypeLiteral<?> javaType;

  private static Type create(String name, Class<?> javaType) {
    return new Type(name, TypeLiteral.get(javaType));
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
      return this == EMPTY_SET || this == STRING_SET || this == FILE_SET;
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

  public static ImmutableList<Type> allowedForSetElem() {
    return SET_ELEM_TYPES;
  }

  public static ImmutableList<TypeLiteral<?>> javaTypesAllowedForResult() {
    return RESULT_JAVA_TYPES;
  }

  public static ImmutableList<TypeLiteral<?>> javaTypesAllowedForParam() {
    return PARAM_JAVA_TYPES;
  }

  public static Type javaParamTypetoType(TypeLiteral<?> javaType) {
    return JAVA_PARAM_TO_SMOOTH.get(javaType);
  }

  public static Type javaResultTypetoType(TypeLiteral<?> javaType) {
    return JAVA_RESULT_TO_SMOOTH.get(javaType);
  }

  private static ImmutableList<TypeLiteral<?>> toJavaTypes(Iterable<Type> types) {
    Builder<TypeLiteral<?>> builder = ImmutableList.builder();

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
}
