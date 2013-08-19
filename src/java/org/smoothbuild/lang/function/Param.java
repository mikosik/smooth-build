package org.smoothbuild.lang.function;

import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Files;

public class Param<T> {
  public static final ParamToNameFunction PARAM_TO_NAME = new ParamToNameFunction();

  private final Type type;
  private final String name;
  private T value;
  private boolean isSet;

  public static Param<String> stringParam(String name) {
    return new Param<String>(Type.STRING, name);
  }

  public static Param<File> fileParam(String name) {
    return new Param<File>(Type.FILE, name);
  }

  public static Param<Files> filesParam(String name) {
    return new Param<Files>(Type.FILES, name);
  }

  protected Param(Type type, String name) {
    this.type = type;
    this.name = name;
    this.value = null;
    this.isSet = false;
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  public void set(T value) {
    this.value = value;
    this.isSet = true;
  }

  public boolean isSet() {
    return isSet;
  }

  public T get() {
    return value;
  }

  @Override
  public String toString() {
    return "Param(" + type.name() + ": " + name + ")";
  }

  private static class ParamToNameFunction implements
      com.google.common.base.Function<Param<?>, String> {
    public String apply(Param<?> param) {
      return param.name();
    }
  }
}
