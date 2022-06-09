package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TKind;

public sealed interface AstNode
    permits GenericAstNode, MonoAstNode, PolyAstNode, MonoObjN, NamedN, ObjN, RefableN {
  public Loc loc();

  public Optional<? extends TKind> typeO();
}
