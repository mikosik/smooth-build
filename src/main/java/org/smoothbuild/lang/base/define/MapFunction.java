package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunction.parameter;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.type.ItemSignature.itemSignature;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Typing;

import com.google.common.collect.ImmutableList;

public class MapFunction extends Function {
  public static final String MAP_FUNCTION_NAME = "map";

  public MapFunction(Typing typing, ModulePath modulePath) {
    this(typing, modulePath, typing.variable("E"), typing.variable("R"));
  }

  public MapFunction(
      Typing typing, ModulePath modulePath, Type inputElemType, Type resultElemType) {
    this(typing.arrayT(resultElemType),
        typing.arrayT(inputElemType),
        typing.functionT(resultElemType, list(itemSignature(inputElemType))),
        modulePath);
  }

  private MapFunction(ArrayType resultType, ArrayType inputArrayType,
      FunctionType mappingFunctionType, ModulePath modulePath) {
    super(resultType,
        modulePath,
        MAP_FUNCTION_NAME,
        createParameters(modulePath, inputArrayType, mappingFunctionType), internal());
  }

  private static ImmutableList<Item> createParameters(ModulePath modulePath,
      ArrayType inputArrayType, FunctionType mappingFunctionType) {
    return list(
        parameter(inputArrayType, modulePath, "array"),
        parameter(mappingFunctionType, modulePath, "function"));
  }
}
