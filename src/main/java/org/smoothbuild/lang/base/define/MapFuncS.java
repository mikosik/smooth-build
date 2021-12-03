package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public final class MapFuncS extends FuncS {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFuncS(ModPath modPath, TypeFactoryS factory) {
    this(modPath, factory.var("E"), factory.var("R"), factory);
  }

  public MapFuncS(ModPath modPath, TypeS inputElemT, TypeS resElemT, TypeFactoryS factory) {
    this(factory.array(resElemT),
        factory.array(inputElemT),
        factory.func(resElemT, list(inputElemT)), modPath,
        factory);
  }

  private MapFuncS(ArrayTS resT, ArrayTS inputArrayT, FuncTS mappingFuncT, ModPath modPath,
      TypeFactoryS factory) {
    this(resT, createParams(modPath, inputArrayT, mappingFuncT), modPath, factory);
  }

  private MapFuncS(ArrayTS resT, NList<ItemS> params, ModPath modPath, TypeFactoryS factory) {
    super(factory.func(
        resT, map(params, DefinedS::type)), modPath, MAP_FUNCTION_NAME, params, internal());
  }

  private static NList<ItemS> createParams(
      ModPath modPath, ArrayTS inputArrayT, FuncTS mappingFuncT) {
    return nList(
        new ItemS(inputArrayT, modPath, "array", Optional.empty(), internal()),
        new ItemS(mappingFuncT, modPath, "func", Optional.empty(), internal()));
  }
}
