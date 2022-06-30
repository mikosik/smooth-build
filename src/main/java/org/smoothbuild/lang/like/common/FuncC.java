package org.smoothbuild.lang.like.common;

import java.util.Optional;

import org.smoothbuild.util.collect.NList;

public interface FuncC extends TopRefableC {
  public Optional<NList<ParamC>> paramsC();
}
