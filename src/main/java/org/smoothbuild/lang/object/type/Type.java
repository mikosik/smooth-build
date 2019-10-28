package org.smoothbuild.lang.object.type;

import java.util.List;

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

  public Type coreType();

  public <T extends Type> T replaceCoreType(T coreType);

  public int coreDepth();

  public Type changeCoreDepthBy(int delta);

  public boolean isGeneric();

  public boolean isArray();

  public boolean isNothing();

  public List<? extends Type> hierarchy();

  public boolean isAssignableFrom(Type type);

  public boolean isParamAssignableFrom(Type type);

  public Type commonSuperType(Type type);
}
