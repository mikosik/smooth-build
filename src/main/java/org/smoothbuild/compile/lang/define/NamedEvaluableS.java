package org.smoothbuild.compile.lang.define;

import org.smoothbuild.util.collect.Named;

public abstract sealed interface NamedEvaluableS extends EvaluableS, Named
    permits FuncS, ValS {
  public ModPath modPath();
}
