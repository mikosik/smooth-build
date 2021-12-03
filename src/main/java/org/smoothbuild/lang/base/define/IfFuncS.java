package org.smoothbuild.lang.base.define;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public final class IfFuncS extends FuncS {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFuncS(ModPath modPath, TypeFactoryS factory) {
    this(factory.var("A"), factory.bool(), modPath, factory);
  }

  private IfFuncS(TypeS resType, TypeS boolType, ModPath modPath, TypeFactoryS factory) {
    this(resType, createParams(resType, boolType, modPath), modPath, factory);
  }

  private IfFuncS(TypeS resType, NList<ItemS> params, ModPath modPath, TypeFactoryS factory) {
    super(funcType(resType, params, factory), modPath, IF_FUNCTION_NAME, params, internal());
  }

  private static FuncTS funcType(TypeS resType, NList<ItemS> params, TypeFactoryS factory) {
    return factory.func(resType, map(params, DefinedS::type));
  }

  private static NList<ItemS> createParams(TypeS resType, TypeS boolType, ModPath modPath) {
    return nList(
        new ItemS(boolType, modPath, "condition", empty(), internal()),
        new ItemS(resType, modPath, "then", empty(), internal()),
        new ItemS(resType, modPath, "else", empty(), internal()));
  }
}
