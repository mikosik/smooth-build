package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

public sealed interface Parsed
    permits GenericP, MonoP, GenericNamedP, NamedP, ObjP, RefableP {
  public Loc loc();

  public Optional<? extends TypeS> typeS();
}
