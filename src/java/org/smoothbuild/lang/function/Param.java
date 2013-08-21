package org.smoothbuild.lang.function;

import static com.google.common.base.Preconditions.checkNotNull;

public class Param {
  public static final ParamToNameFunction PARAM_TO_NAME = new ParamToNameFunction();

  private final Type type;
  private final String name;

  public static Param param(Type type, String name) {
    return new Param(type, name);
  }

  protected Param(Type type, String name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  @Override
  public final boolean equals(Object object) {
    if (!(object instanceof Param)) {
      return false;
    }
    Param that = (Param) object;
    return this.type.equals(that.type) && this.name.equals(that.name);
  }

  @Override
  public final int hashCode() {
    return 17 * type.hashCode() + name.hashCode();
  }

  @Override
  public String toString() {
    return "Param(" + type.name() + ": " + name + ")";
  }

  private static class ParamToNameFunction implements
      com.google.common.base.Function<Param, String> {
    public String apply(Param param) {
      return param.name();
    }
  }
}
