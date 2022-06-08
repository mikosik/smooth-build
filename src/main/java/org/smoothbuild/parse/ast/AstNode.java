package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TKind;

public sealed interface AstNode
    permits MonoAstNode, ObjN {
  public Loc loc();

  public Optional<? extends TKind> typeS();
}
