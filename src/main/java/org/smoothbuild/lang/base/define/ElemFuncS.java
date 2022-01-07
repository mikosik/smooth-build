package org.smoothbuild.lang.base.define;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public final class ElemFuncS extends FuncS {
  public static final String ELEM_FUNCTION_NAME = "elem";

  public ElemFuncS(ModPath modPath, TypeFactoryS factory) {
    this(factory.var("A"), factory.int_(), modPath, factory);
  }

  private ElemFuncS(TypeS resT, IntTS intT, ModPath modPath, TypeFactoryS factory) {
    this(resT, createParams(factory.array(resT), intT, modPath), modPath, factory);
  }

  private ElemFuncS(TypeS resT, NList<ItemS> params, ModPath modPath, TypeFactoryS factory) {
    super(funcT(resT, params, factory), modPath, ELEM_FUNCTION_NAME, params, internal());
  }

  private static FuncTS funcT(TypeS resT, NList<ItemS> params, TypeFactoryS factory) {
    return factory.func(resT, map(params, DefinedS::type));
  }

  private static NList<ItemS> createParams(ArrayTS arrayT, IntTS intT, ModPath modPath) {
    return nList(
        new ItemS(arrayT, modPath, "array", empty(), internal()),
        new ItemS(intT, modPath, "index", empty(), internal()));
  }
}
