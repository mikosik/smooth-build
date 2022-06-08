package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tal;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class CnstN extends Tal implements ObjN
    permits BlobN, IntN, StringN {
  public CnstN(TypeS type, Loc loc) {
    super(type, loc);
  }

  @Override
  public Optional<TypeS> typeS() {
    return Optional.of(type());
  }
}
