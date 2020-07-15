package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.base.SObject;

/**
 * Type in smooth language.
 */
public interface Type {
  public String name();

  public TypeKind kind();

  public Class<? extends SObject> jType();

  public boolean isArray();

  public boolean isNothing();
}
