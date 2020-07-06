package org.smoothbuild.lang.base.type;

import java.util.List;
import java.util.Optional;

public abstract class Type {
  private final String name;

  protected Type(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public String q() {
    return "'" + name + "'";
  }

  public abstract boolean isGeneric();

  public abstract boolean isArray();

  public abstract boolean isNothing();

  public abstract Type superType();

  public abstract Type coreType();

  public abstract <T extends Type> T replaceCoreType(T coreType);

  public abstract int coreDepth();

  public abstract Type changeCoreDepthBy(int coreDepth);

  public abstract List<? extends Type> hierarchy();

  public abstract boolean isAssignableFrom(Type type);

  public abstract boolean isParamAssignableFrom(Type type);

  public abstract Optional<Type> commonSuperType(Type that);

  @Override
  public String toString() {
    return "Type(\"" + name() + "\")";
  }
}
