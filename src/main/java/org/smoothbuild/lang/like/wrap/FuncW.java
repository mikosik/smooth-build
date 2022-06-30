package org.smoothbuild.lang.like.wrap;

import java.util.Optional;

import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.like.common.FuncC;
import org.smoothbuild.lang.like.common.ParamC;
import org.smoothbuild.util.collect.NList;

public class FuncW extends TopRefableW implements FuncC {
  private final FuncS funcS;

  public FuncW(FuncS funcS) {
    super(funcS);
    this.funcS = funcS;
  }

  @Override
  public Optional<NList<ParamC>> paramsC() {
    return Optional.of(funcS.params().map(p -> new ParamC(p.sig(), p.body().map(MonoObjW::new))));
  }
}
