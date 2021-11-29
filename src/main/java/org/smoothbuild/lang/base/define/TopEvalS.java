package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Top level evaluable.
 */
public sealed abstract class TopEvalS extends EvalS implements Nal
    permits FunctionS, ValueS {
  public TopEvalS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}
