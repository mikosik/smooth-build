package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Expression in smooth language.
 */
public interface ExprS extends Nal {
  public TypeS type();

  @Override
  public String name();

  @Override
  public Location location();
}
