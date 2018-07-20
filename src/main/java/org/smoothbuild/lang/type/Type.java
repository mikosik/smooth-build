package org.smoothbuild.lang.type;

import java.util.List;

import org.smoothbuild.lang.value.Value;

/**
 * Type in smooth language.
 */
public interface Type {
  public Type superType();

  public String name();

  public Class<? extends Value> jType();

  public Type coreType();

  public int coreDepth();

  public boolean isArray();

  public boolean isNothing();

  public List<? extends Type> hierarchy();

  public boolean isAssignableFrom(Type argType);

  public Type commonSuperType(Type type);
}
