package org.smoothbuild.lang.function;

import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Files;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class Type<T> {
  public static final Type<String> STRING = create("String", String.class);
  public static final Type<File> FILE = create("File", File.class);
  public static final Type<Files> FILES = create("Files", Files.class);

  static final ImmutableList<Type<?>> ALL_TYPES = createAllTypes();
  static final ImmutableList<Class<?>> ALL_JAVA_TYPES = createAllJavaTypes();

  static final ImmutableMap<Class<?>, Type<?>> JAVA_TO_SMOOTH = createMap();

  private final String name;
  private final Class<T> javaType;

  private static <T> Type<T> create(String name, Class<T> klass) {
    return new Type<T>(name, klass);
  }

  private Type(String name, Class<T> javaType) {
    this.name = name;
    this.javaType = javaType;
  }

  public String name() {
    return name;
  }

  public Class<T> javaType() {
    return javaType;
  }

  public static ImmutableList<Type<?>> allTypes() {
    return ALL_TYPES;
  }

  public static ImmutableList<Class<?>> allJavaTypes() {
    return ALL_JAVA_TYPES;
  }

  public static <T> Type<T> toType(Class<T> klass) {
    @SuppressWarnings("unchecked")
    Type<T> result = (Type<T>) JAVA_TO_SMOOTH.get(klass);
    return result;
  }

  private static ImmutableList<Type<?>> createAllTypes() {
    Builder<Type<?>> builder = ImmutableList.builder();

    builder.add(STRING);
    builder.add(FILE);
    builder.add(FILES);

    return builder.build();
  }

  private static ImmutableList<Class<?>> createAllJavaTypes() {
    Builder<Class<?>> builder = ImmutableList.builder();

    for (Type<?> type : ALL_TYPES) {
      builder.add(type.javaType);
    }

    return builder.build();
  }

  private static ImmutableMap<Class<?>, Type<?>> createMap() {
    ImmutableMap.Builder<Class<?>, Type<?>> builder = ImmutableMap.builder();

    for (Type<?> type : ALL_TYPES) {
      builder.put(type.javaType, type);
    }

    return builder.build();
  }
}
