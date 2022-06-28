package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public final class ExplicitArgP extends ArgP {
  public ExplicitArgP(Optional<String> name, ObjP obj, Loc loc) {
    super(name, obj, loc);
  }

  @Override
  public String nameSanitized() {
    return declaresName() ? name() : "<nameless>";
  }
}
