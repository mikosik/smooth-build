package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunction.parameter;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

public class MapFunction extends Function {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFunction(ModulePath modulePath, Typing typing) {
    this(modulePath, typing.variable("E"), typing.variable("R"), typing);
  }

  public MapFunction(
      ModulePath modulePath, Type inputElemType, Type resultElemType, Typing typing) {
    this(typing.array(resultElemType),
        typing.array(inputElemType),
        typing.function(resultElemType, list(inputElemType)),
        modulePath,
        typing);
  }

  private MapFunction(ArrayType resultType, ArrayType inputArrayType,
      FunctionType mappingFunctionType, ModulePath modulePath, Typing typing) {
    this(resultType, createParameters(modulePath, inputArrayType, mappingFunctionType), modulePath,
        typing);
  }

  private MapFunction(ArrayType resultType, ImmutableList<Item> parameters, ModulePath modulePath,
      Typing typing) {
    super(
        typing.function(resultType, toTypes(parameters)),
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
