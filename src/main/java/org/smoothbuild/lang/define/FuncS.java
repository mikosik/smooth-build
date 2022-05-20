package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Panal;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class FuncS extends Panal implements TopRefableS
    permits MonoFuncS, PolyFuncS {
  private final NList<ItemS> params;

  public FuncS(ModPath modPath, String name, NList<ItemS> params, Loc loc) {
    super(modPath, name, loc);
    this.params = requireNonNull(params);
  }

  public NList<ItemS> params() {
    return params;
  }

  protected String paramsToString() {
    return toCommaSeparatedString(params, FuncS::paramToString);
  }

  private static String paramToString(ItemS itemS) {
    return itemS.type().name() + " " + itemS.name();
  }
}
