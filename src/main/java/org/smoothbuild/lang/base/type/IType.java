package org.smoothbuild.lang.base.type;

import java.util.List;
import java.util.Optional;

public abstract class IType {
  private final String name;

  protected IType(String name) {
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

  public abstract IType superType();

  public abstract IType coreType();

  public abstract <T extends IType> T replaceCoreType(T coreType);

  public abstract int coreDepth();

  public abstract IType changeCoreDepthBy(int coreDepth);

  public abstract List<? extends IType> hierarchy();

  public abstract boolean isAssignableFrom(IType type);

  public abstract boolean isParamAssignableFrom(IType type);

  public abstract Optional<IType> commonSuperType(IType that);

  @Override
  public String toString() {
    return "Type(\"" + name() + "\")";
  }
}
