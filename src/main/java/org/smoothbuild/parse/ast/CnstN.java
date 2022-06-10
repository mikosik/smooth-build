package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tal;
import org.smoothbuild.lang.type.MonoTS;

public sealed abstract class CnstN extends Tal implements MonoObjN
    permits BlobN, IntN, StringN {
  public CnstN(MonoTS type, Loc loc) {
    super(type, loc);
  }

  @Override
  public Optional<MonoTS> typeO() {
    return Optional.of(type());
  }
}
