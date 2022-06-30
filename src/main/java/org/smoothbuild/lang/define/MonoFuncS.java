package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class MonoFuncS extends FuncS implements MonoTopRefableS, CnstS
    permits AnnFuncS, DefFuncS, SyntCtorS {
  private final MonoFuncTS type;

  public MonoFuncS(MonoFuncTS type, ModPath modPath, String name, NList<ItemS> params, Loc loc) {
    super(modPath, name, params, loc);
    this.type = type;
  }

  @Override
  public MonoFuncTS type() {
    return type;
  }

  public MonoTS resT() {
    return type().res();
  }

  public boolean canBeCalledArgless() {
    return params().stream()
        .allMatch(p -> p.body().isPresent());
  }

  protected String signature() {
    return resT().name() + " " + name() + "(" + paramsToString() + ")";
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
