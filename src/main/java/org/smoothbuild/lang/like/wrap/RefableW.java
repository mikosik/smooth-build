package org.smoothbuild.lang.like.wrap;

import java.util.Optional;

import org.smoothbuild.lang.define.RefableS;
import org.smoothbuild.lang.like.common.RefableC;
import org.smoothbuild.lang.type.TypeS;

public class RefableW implements RefableC {
  private final RefableS refableS;

  public RefableW(RefableS refableS) {
    this.refableS = refableS;
  }

  @Override
  public Optional<? extends TypeS> typeS() {
    return Optional.of(refableS.type());
  }

  @Override
  public String name() {
    return refableS.name();
  }
}
