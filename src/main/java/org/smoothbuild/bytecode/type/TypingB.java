package org.smoothbuild.bytecode.type;

import javax.inject.Inject;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.type.Typing;

public class TypingB extends Typing<TypeB> {
  @Inject
  public TypingB(CatDb factory) {
    super(factory);
  }
}
