package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunction.parameter;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;

import com.google.common.collect.ImmutableList;

public class MapFunction extends Function {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFunction(ModulePath modulePath, TypeFactory factory) {
    this(modulePath, factory.variable("E"), factory.variable("R"), factory);
  }

  public MapFunction(
      ModulePath modulePath, Type inputElemType, Type resultElemType, TypeFactory factory) {
    this(factory.array(resultElemType),
        factory.array(inputElemType),
        factory.function(resultElemType, list(inputElemType)),
        modulePath,
        factory);
  }

  private MapFunction(ArrayType resultType, ArrayType inputArrayType,
      FunctionType mappingFunctionType, ModulePath modulePath, TypeFactory factory) {
    this(resultType, createParameters(modulePath, inputArrayType, mappingFunctionType), modulePath,
        factory);
  }

  private MapFunction(ArrayType resultType, ImmutableList<Item> parameters, ModulePath modulePath,
      TypeFactory factory) {
    super(
        factory.function(resultType, toTypes(parameters)),
        modulePath,
        MAP_FUNCTION_NAME,
        parameters,
        internal()
    );
  }

  private static ImmutableList<Item> createParameters(ModulePath modulePath,
      ArrayType inputArrayType, FunctionType mappingFunctionType) {
    return list(
        parameter(inputArrayType, modulePath, "array"),
        parameter(mappingFunctionType, modulePath, "function"));
  }
}
