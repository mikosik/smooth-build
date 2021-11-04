package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunction.parameter;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.impl.ArraySType;
import org.smoothbuild.lang.base.type.impl.FunctionSType;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

public class MapFunction extends Function {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFunction(ModulePath modulePath, TypeFactory factory) {
    this(modulePath, (TypeS) factory.variable("E"), (TypeS) factory.variable("R"), factory);
  }

  public MapFunction(
      ModulePath modulePath, TypeS inputElemType, TypeS resultElemType, TypeFactory factory) {
    this((ArraySType) factory.array(resultElemType),
        (ArraySType) factory.array(inputElemType),
        (FunctionSType) factory.function(resultElemType, list(inputElemType)),
        modulePath,
        factory);
  }

  private MapFunction(ArraySType resultType, ArraySType inputArrayType,
      FunctionSType mappingFunctionType, ModulePath modulePath, TypeFactory factory) {
    this(resultType, createParameters(modulePath, inputArrayType, mappingFunctionType), modulePath,
        factory);
  }

  private MapFunction(ArraySType resultType, ImmutableList<Item> parameters, ModulePath modulePath,
      TypeFactory factory) {
    super(
        (FunctionSType) factory.function(resultType, toTypes(parameters)),
        modulePath,
        MAP_FUNCTION_NAME,
        parameters,
        internal()
    );
  }

  private static ImmutableList<Item> createParameters(ModulePath modulePath,
      ArraySType inputArrayType, FunctionSType mappingFunctionType) {
    return list(
        parameter(inputArrayType, modulePath, "array"),
        parameter(mappingFunctionType, modulePath, "function"));
  }
}
