package org.smoothbuild.db.object.type;

import javax.inject.Inject;

import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.Typing;

public class TypingH extends Typing<TypeHV> {
  @Inject
  public TypingH(TypeHDb factory) {
    super(factory);
  }
}
