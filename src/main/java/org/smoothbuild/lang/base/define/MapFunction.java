package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunction.parameter;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

public class MapFunction extends FunctionS {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFunction(ModulePath modulePath, TypeFactoryS factory) {
    this(modulePath, factory.variable("E"), factory.variable("R"), factory);
  }

  public MapFunction(
      ModulePath modulePath, TypeS inputElemType, TypeS resultElemType, TypeFactoryS factory) {
    this(factory.array(resultElemType),
        factory.array(inputElemType),
        factory.function(resultElemType, list(inputElemType)),
        modulePath,
        factory);
  }

  private MapFunction(ArrayTypeS resultType, ArrayTypeS inputArrayType,
      FunctionTypeS mappingFunctionType, ModulePath modulePath, TypeFactoryS factory) {
    this(resultType, createParameters(modulePath, inputArrayType, mappingFunctionType), modulePath,
        factory);
  }

  private MapFunction(ArrayTypeS resultType, ImmutableList<Item> parameters, ModulePath modulePath,
      TypeFactoryS factory) {
    super(
        factory.function(resultType, toTypes(parameters)),
        modulePath,
        MAP_FUNCTION_NAME,
        parameters,
        internal()
    );
  }

  private static ImmutableList<Item> createParameters(ModulePath modulePath,
      ArrayTypeS inputArrayType, FunctionTypeS mappingFunctionType) {
    return list(
        parameter(inputArrayType, modulePath, "array"),
        parameter(mappingFunctionType, modulePath, "function"));
  }
}
