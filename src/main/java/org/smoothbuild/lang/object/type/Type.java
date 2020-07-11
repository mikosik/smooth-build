package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.base.SObject;

/**
 * Type in smooth language.
 */
public interface Type {
  public Type superType();

  public String name();

  /**
   * @return single quoted name of this type.
   */
  public String q();

  public Class<? extends SObject> jType();

  public boolean isArray();

  public boolean isNothing();
}
