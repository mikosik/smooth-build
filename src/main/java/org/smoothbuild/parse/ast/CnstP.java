package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tal;
import org.smoothbuild.lang.type.MonoTS;

public sealed abstract class CnstP extends Tal implements MonoObjP
    permits BlobP, IntP, StringP {
  public CnstP(MonoTS type, Loc loc) {
    super(type, loc);
  }

  @Override
  public Optional<MonoTS> typeO() {
    return Optional.of(type());
  }
}
