package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.Type;

/**
 * Expression in smooth language.
 */
public interface Expression {
  public Type type();

  public Location location();
}
