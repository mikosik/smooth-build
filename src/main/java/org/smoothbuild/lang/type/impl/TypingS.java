package org.smoothbuild.lang.type.impl;

import javax.inject.Inject;

import org.smoothbuild.lang.type.Typing;

public class TypingS extends Typing<TypeS> {
  @Inject
  public TypingS(TypeFS factory) {
    super(factory);
  }
}
