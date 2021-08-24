package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.IfFunction.parameter;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.type.ItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.Types.arrayT;
import static org.smoothbuild.lang.base.type.Types.functionT;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Variable;

import com.google.common.collect.ImmutableList;

public class MapFunction extends Function {
  public static final String MAP_FUNCTION_NAME = "map";
  private static final Type R = new Variable("R");
  private static final Type E = new Variable("E");
  private static final Type RESULT_TYPE = arrayT(R);

  public MapFunction(ModulePath modulePath) {
    super(RESULT_TYPE, modulePath, MAP_FUNCTION_NAME, createParameters(modulePath), internal());
  }

  private static ImmutableList<Item> createParameters(ModulePath modulePath) {
    return list(
        parameter(arrayT(E), modulePath, "array"),
        parameter(functionT(R, list(itemSignature(E))), modulePath, "function"));
  }
}
