package org.smoothbuild.lang.base.type.impl;

import javax.inject.Inject;

import org.smoothbuild.lang.base.type.Typing;

public class TypingS extends Typing<TypeS> {
  @Inject
  public TypingS(TypeSF factory) {
    super(factory);
  }
}
