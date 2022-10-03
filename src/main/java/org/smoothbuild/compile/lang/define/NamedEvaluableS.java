package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tal;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.util.collect.Named;

public abstract sealed class NamedEvaluableS extends Tal implements EvaluableS, Named
    permits FuncS, NamedValS {

  private final ModPath modPath;
  private final String name;

  public NamedEvaluableS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, loc);
    this.modPath = modPath;
    this.name = name;
  }

  public ModPath modPath() {
    return modPath;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String label() {
    return name();
  }
}
