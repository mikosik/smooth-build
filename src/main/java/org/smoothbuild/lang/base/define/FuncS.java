package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class FuncS extends TopEvalS
    permits CtorS, DefFuncS, IfFuncS, MapFuncS, NatFuncS {
  public static final String PARENTHESES = "()";
  private final NList<Item> params;

  public FuncS(FuncTypeS type, ModPath modPath, String name, NList<Item> params,
      Loc loc) {
    super(type, modPath, name, loc);
    this.params = requireNonNull(params);
  }

  @Override
  public FuncTypeS type() {
    return (FuncTypeS) super.type();
  }

  @Override
  public String extendedName() {
    return name() + PARENTHESES;
  }

  public TypeS resType() {
    return type().res();
  }

  public NList<Item> params() {
    return params;
  }

  public boolean canBeCalledArgless() {
    return params.stream()
        .allMatch(p -> p.defaultVal().isPresent());
  }

  protected String signature() {
    return resType().name() + " " + name() + "(" + paramsToString() + ")";
  }

  protected String paramsToString() {
    return toCommaSeparatedString(params, Defined::typeAndName);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
