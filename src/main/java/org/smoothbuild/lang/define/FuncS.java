package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class FuncS extends RefableObjS permits AnnFuncS, DefFuncS, SyntCtorS {
  private final NList<ItemS> params;

  public FuncS(FuncTS type, ModPath modPath, String name, NList<ItemS> params, Loc loc) {
    super(type, modPath, name, loc);
    this.params = requireNonNull(params);
  }

  @Override
  public FuncTS type() {
    return (FuncTS) super.type();
  }

  public TypeS resT() {
    return type().res();
  }

  public NList<ItemS> params() {
    return params;
  }

  public boolean canBeCalledArgless() {
    return params.stream()
        .allMatch(p -> p.body().isPresent());
  }

  protected String signature() {
    return resT().name() + " " + name() + "(" + paramsToString() + ")";
  }

  protected String paramsToString() {
    return toCommaSeparatedString(params, FuncS::paramToString);
  }

  private static String paramToString(ItemS itemS) {
    return itemS.type().name() + " " + itemS.name();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
