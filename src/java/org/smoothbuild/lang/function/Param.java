package org.smoothbuild.lang.function;

import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.FilesRo;

public class Param<T> {
  public static final ParamToNameFunction PARAM_TO_NAME = new ParamToNameFunction();

  private final String typeName;
  private final String name;
  private T value;
  private boolean isSet;

  public static Param<String> stringParam(String name) {
    return new Param<String>("String", name);
  }

  public static Param<FileRo> fileParam(String name) {
    return new Param<FileRo>("File", name);
  }

  public static Param<FilesRo> filesParam(String name) {
    return new Param<FilesRo>("Files", name);
  }

  protected Param(String typeName, String name) {
    this.typeName = typeName;
    this.name = name;
    this.value = null;
    this.isSet = false;
  }

  public String typeName() {
    return typeName;
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
    return "Param(" + typeName + ": " + name + ")";
  }

  private static class ParamToNameFunction implements
      com.google.common.base.Function<Param<?>, String> {
    public String apply(Param<?> param) {
      return param.name();
    }
  }
}
