package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public final class ExplicitArgN extends ArgN {
  public ExplicitArgN(Optional<String> name, ObjN obj, Loc loc) {
    super(name, obj, loc);
  }

  @Override
  public String nameSanitized() {
    return declaresName() ? name() : "<nameless>";
  }
}
