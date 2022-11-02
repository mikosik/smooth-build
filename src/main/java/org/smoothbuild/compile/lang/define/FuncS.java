package org.smoothbuild.compile.lang.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class FuncS extends Tanal implements EvaluableS
    permits AnnFuncS, DefFuncS, SyntCtorS {
  private final NList<ItemS> params;

  public FuncS(FuncTS type, String name, NList<ItemS> params, Loc loc) {
    super(type, name, loc);
    this.params = requireNonNull(params);
  }

  public NList<ItemS> params() {
    return params;
  }

  protected String paramsToString() {
    return toCommaSeparatedString(params, FuncS::paramToString);
  }

  private static String paramToString(ItemS itemS) {
    return itemS.type().name() + " " + itemS.name() + itemS.defaultVal().map(b -> " = " + b).orElse("");
  }

  @Override
  public FuncTS type() {
    return (FuncTS) super.type();
  }

  public TypeS resT() {
    return type().res();
  }

  public boolean canBeCalledArgless() {
    return params().stream()
        .allMatch(p -> p.defaultVal().isPresent());
  }

  protected String signature() {
    return resT().name() + " " + name() + "(" + paramsToString() + ")";
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
