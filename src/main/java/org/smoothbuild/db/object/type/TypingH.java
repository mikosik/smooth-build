package org.smoothbuild.db.object.type;

import javax.inject.Inject;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.Typing;

public class TypingH extends Typing<TypeH> {
  @Inject
  public TypingH(CatDb factory) {
    super(factory);
  }
}
