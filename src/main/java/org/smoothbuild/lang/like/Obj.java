package org.smoothbuild.lang.like;

import java.util.Optional;

import org.smoothbuild.lang.type.TKind;

/**
 * Literal or expression.
 */
public interface Obj {
  public Optional<? extends TKind> typeO();
}
