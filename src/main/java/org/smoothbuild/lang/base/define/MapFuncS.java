package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public final class MapFuncS extends FuncS {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFuncS(ModPath modPath, TypeFactoryS factory) {
    this(modPath, factory.var("E"), factory.var("R"), factory);
  }

  public MapFuncS(ModPath modPath, TypeS inputElemType, TypeS resElemType, TypeFactoryS factory) {
    this(factory.array(resElemType),
        factory.array(inputElemType),
        factory.func(resElemType, list(inputElemType)), modPath,
        factory);
  }

  private MapFuncS(ArrayTypeS resType, ArrayTypeS inputArrayType,
      FuncTypeS mappingFuncType, ModPath modPath, TypeFactoryS factory) {
    this(resType, createParams(modPath, inputArrayType, mappingFuncType), modPath,
        factory);
  }

  private MapFuncS(ArrayTypeS resType, NList<ItemS> params, ModPath modPath, TypeFactoryS factory) {
    super(
        factory.func(resType, map(params, DefinedS::type)), modPath,
        MAP_FUNCTION_NAME,
        params,
        internal()
    );
  }

  private static NList<ItemS> createParams(
      ModPath modPath, ArrayTypeS inputArrayType, FuncTypeS mappingFuncType) {
    return nList(
        new ItemS(inputArrayType, modPath, "array", Optional.empty(), internal()),
        new ItemS(mappingFuncType, modPath, "func", Optional.empty(), internal()));
  }
}
