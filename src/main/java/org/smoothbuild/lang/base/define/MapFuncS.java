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

  public MapFuncS(ModulePath modulePath, TypeFactoryS factory) {
    this(modulePath, factory.variable("E"), factory.variable("R"), factory);
  }

  public MapFuncS(
      ModulePath modulePath, TypeS inputElemType, TypeS resultElemType, TypeFactoryS factory) {
    this(factory.array(resultElemType),
        factory.array(inputElemType),
        factory.abstFunc(resultElemType, list(inputElemType)),
        modulePath,
        factory);
  }

  private MapFuncS(ArrayTypeS resultType, ArrayTypeS inputArrayType,
      FuncTypeS mappingFuncType, ModulePath modulePath, TypeFactoryS factory) {
    this(resultType, createParams(modulePath, inputArrayType, mappingFuncType), modulePath,
        factory);
  }

  private MapFuncS(ArrayTypeS resultType, NList<Item> params, ModulePath modulePath,
      TypeFactoryS factory) {
    super(
        factory.abstFunc(resultType, map(params, Defined::type)),
        modulePath,
        MAP_FUNCTION_NAME,
        params,
        internal()
    );
  }

  private static NList<Item> createParams(ModulePath modulePath,
      ArrayTypeS inputArrayType, FuncTypeS mappingFuncType) {
    return nList(
        new Item(inputArrayType, modulePath, "array", Optional.empty(), internal()),
        new Item(mappingFuncType, modulePath, "func", Optional.empty(), internal()));
  }
}
