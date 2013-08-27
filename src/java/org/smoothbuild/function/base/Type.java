package org.smoothbuild.function.base;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Files;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class Type {
  public static final Type STRING = create("String", String.class);
  public static final Type FILE = create("File", File.class);
  public static final Type FILES = create("Files", Files.class);
  public static final Type VOID = create("Void", Void.TYPE);

  static final ImmutableList<Type> RESULT_TYPES = ImmutableList.of(STRING, FILE, FILES, VOID);
  static final ImmutableList<Type> PARAM_TYPES = ImmutableList.of(STRING, FILE, FILES);

  static final ImmutableList<Class<?>> RESULT_JAVA_TYPES = toJavaTypes(RESULT_TYPES);
  static final ImmutableList<Class<?>> PARAM_JAVA_TYPES = toJavaTypes(PARAM_TYPES);

  static final ImmutableMap<Class<?>, Type> JAVA_PARAM_TO_SMOOTH = javaToTypeMap(PARAM_TYPES);
  static final ImmutableMap<Class<?>, Type> JAVA_RESULT_TO_SMOOTH = javaToTypeMap(RESULT_TYPES);

  private final String name;
  private final Class<?> javaType;

  private static Type create(String name, Class<?> klass) {
    return new Type(name, klass);
  }

  private Type(String name, Class<?> javaType) {
    this.name = name;
    this.javaType = javaType;
  }

  public String name() {
    return name;
  }

  public Class<?> javaType() {
    return javaType;
  }

  public boolean isAssignableFrom(Type type) {
    return this.equals(type);
  }

  @Override
  public final boolean equals(Object object) {
    return this == object;
  }

  @Override
  public final int hashCode() {
    return name.hashCode();
  }

  public static ImmutableList<Class<?>> javaTypesAllowedForResult() {
    return RESULT_JAVA_TYPES;
  }

  public static ImmutableList<Class<?>> javaTypesAllowedForParam() {
    return PARAM_JAVA_TYPES;
  }

  public static Type javaParamTypetoType(Class<?> klass) {
    return JAVA_PARAM_TO_SMOOTH.get(klass);
  }

  public static Type javaResultTypetoType(Class<?> klass) {
    return JAVA_RESULT_TO_SMOOTH.get(klass);
  }

  private static ImmutableList<Class<?>> toJavaTypes(Iterable<Type> types) {
    Builder<Class<?>> builder = ImmutableList.builder();

    for (Type type : types) {
      builder.add(type.javaType);
    }

    return builder.build();
  }

  private static ImmutableMap<Class<?>, Type> javaToTypeMap(Iterable<Type> types) {
    ImmutableMap.Builder<Class<?>, Type> builder = ImmutableMap.builder();

    for (Type type : types) {
      builder.put(type.javaType, type);
    }

    return builder.build();
  }
}
