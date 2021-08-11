package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Variable;

import com.google.common.collect.ImmutableList;

public class IfFunction extends Function {
  public static final String IF_FUNCTION_NAME = "if";
  private static final Type RESULT_TYPE = new Variable("A");

  public IfFunction(ModulePath modulePath) {
    super(RESULT_TYPE, modulePath, IF_FUNCTION_NAME, createParameters(modulePath), internal());
  }

  private static ImmutableList<Item> createParameters(ModulePath modulePath) {
    return list(
        parameter(bool(), modulePath, "condition"),
        parameter(RESULT_TYPE, modulePath, "then"),
        parameter(RESULT_TYPE, modulePath, "else"));
  }

  public static Item parameter(Type type, ModulePath modulePath, String name) {
    return new Item(type, modulePath, name, Optional.empty(), internal());
  }
}
