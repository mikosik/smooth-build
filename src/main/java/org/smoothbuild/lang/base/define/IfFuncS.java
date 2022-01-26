package org.smoothbuild.lang.base.define;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypeSF;
import org.smoothbuild.util.collect.NList;

public final class IfFuncS extends FuncS {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFuncS(ModPath modPath, TypeSF factory) {
    this(factory.oVar("A"), factory.bool(), modPath, factory);
  }

  private IfFuncS(TypeS resT, TypeS boolT, ModPath modPath, TypeSF factory) {
    this(resT, createParams(resT, boolT, modPath), modPath, factory);
  }

  private IfFuncS(TypeS resT, NList<ItemS> params, ModPath modPath, TypeSF factory) {
    super(funcT(resT, params, factory), modPath, IF_FUNCTION_NAME, params, internal());
  }

  private static FuncTS funcT(TypeS resT, NList<ItemS> params, TypeSF factory) {
    return factory.func(resT, map(params, DefinedS::type));
  }

  private static NList<ItemS> createParams(TypeS resT, TypeS boolT, ModPath modPath) {
    return nList(
        new ItemS(boolT, modPath, "condition", empty(), internal()),
        new ItemS(resT, modPath, "then", empty(), internal()),
        new ItemS(resT, modPath, "else", empty(), internal()));
  }
}
