package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunctionS.parameter;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;

public class MapFunctionS extends FunctionS {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFunctionS(ModulePath modulePath, TypeFactoryS factory) {
    this(modulePath, factory.variable("E"), factory.variable("R"), factory);
  }

  public MapFunctionS(
      ModulePath modulePath, TypeS inputElemType, TypeS resultElemType, TypeFactoryS factory) {
    this(factory.array(resultElemType),
        factory.array(inputElemType),
        factory.function(resultElemType, list(inputElemType)),
        modulePath,
        factory);
  }

  private MapFunctionS(ArrayTypeS resultType, ArrayTypeS inputArrayType,
      FunctionTypeS mappingFunctionType, ModulePath modulePath, TypeFactoryS factory) {
    this(resultType, createParameters(modulePath, inputArrayType, mappingFunctionType), modulePath,
        factory);
  }

  private MapFunctionS(ArrayTypeS resultType, NamedList<Item> parameters, ModulePath modulePath,
      TypeFactoryS factory) {
    super(
        factory.function(resultType, map(parameters, Defined::type)),
        modulePath,
        MAP_FUNCTION_NAME,
        parameters,
        internal()
    );
  }

  private static NamedList<Item> createParameters(ModulePath modulePath,
      ArrayTypeS inputArrayType, FunctionTypeS mappingFunctionType) {
    return namedList(list(
        parameter(inputArrayType, modulePath, "array"),
        parameter(mappingFunctionType, modulePath, "function")));
  }
}
