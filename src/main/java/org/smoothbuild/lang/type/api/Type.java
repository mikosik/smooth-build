package org.smoothbuild.lang.type.api;

import org.smoothbuild.util.collect.Named;

/**
 * This class and all its subclasses are immutable.
 */
public interface Type extends Named {
  public VarSet<?> vars();
}
