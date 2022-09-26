package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Nal;

public sealed interface EvaluableS extends ValS, Nal
    permits FuncS, NamedValS {
  public ModPath modPath();

  @Override
  public default String label() {
    return name();
  }
}
