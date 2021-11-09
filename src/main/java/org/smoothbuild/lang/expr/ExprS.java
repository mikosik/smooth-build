package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Expression in smooth language.
 */
public interface ExprS {
  public TypeS type();

  public Location location();
}
