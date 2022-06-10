package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Panal;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class MonoFuncS extends Panal
    permits AnnFuncS, DefFuncS, SyntCtorS {
  private final MonoFuncTS type;
  private final NList<ItemS> params;

  public MonoFuncS(MonoFuncTS type, ModPath modPath, String name, NList<ItemS> params, Loc loc) {
    super(modPath, name, loc);
    this.type = type;
    this.params = requireNonNull(params);
  }

  public MonoFuncTS type() {
    return type;
  }

  public MonoTS resT() {
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
    return toCommaSeparatedString(params, MonoFuncS::paramToString);
  }

  private static String paramToString(ItemS itemS) {
    return itemS.type().name() + " " + itemS.name();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
