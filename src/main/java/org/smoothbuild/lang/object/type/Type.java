package org.smoothbuild.lang.object.type;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.parse.ast.Named;

/**
 * Type in smooth language.
 */
public interface Type extends Named {
  public Type superType();

  @Override
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

  public Optional<Type> commonSuperType(Type type);
}
