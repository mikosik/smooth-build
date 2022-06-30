package org.smoothbuild.lang.like.wrap;

import java.util.Optional;

import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.like.common.ObjC;
import org.smoothbuild.lang.type.TypeS;

public class MonoObjW implements ObjC {
  private final MonoObjS monoObjS;

  public MonoObjW(MonoObjS monoObjS) {
    this.monoObjS = monoObjS;
  }

  @Override
  public Optional<? extends TypeS> typeS() {
    return Optional.of(monoObjS.type());
  }

  public MonoObjS monoObjS() {
    return monoObjS;
  }
}
