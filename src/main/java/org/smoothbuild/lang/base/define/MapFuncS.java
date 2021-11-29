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
    this(modPath, factory.variable("E"), factory.variable("R"), factory);
  }

  public MapFuncS(
      ModPath modPath, TypeS inputElemType, TypeS resultElemType, TypeFactoryS factory) {
    this(factory.array(resultElemType),
        factory.array(inputElemType),
        factory.abstFunc(resultElemType, list(inputElemType)), modPath,
        factory);
  }

  private MapFuncS(ArrayTypeS resultType, ArrayTypeS inputArrayType,
      FuncTypeS mappingFuncType, ModPath modPath, TypeFactoryS factory) {
    this(resultType, createParams(modPath, inputArrayType, mappingFuncType), modPath,
        factory);
  }

  private MapFuncS(ArrayTypeS resultType, NList<Item> params, ModPath modPath,
      TypeFactoryS factory) {
    super(
        factory.abstFunc(resultType, map(params, Defined::type)), modPath,
        MAP_FUNCTION_NAME,
        params,
        internal()
    );
  }

  private static NList<Item> createParams(ModPath modPath,
      ArrayTypeS inputArrayType, FuncTypeS mappingFuncType) {
    return nList(
        new Item(inputArrayType, modPath, "array", Optional.empty(), internal()),
        new Item(mappingFuncType, modPath, "func", Optional.empty(), internal()));
  }
}
