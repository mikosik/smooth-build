package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public TypeS type();

  public Location location();
}
