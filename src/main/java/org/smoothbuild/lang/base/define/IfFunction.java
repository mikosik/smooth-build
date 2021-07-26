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
  private static final Variable RESULT_TYPE = new Variable("A");

  public IfFunction(ModulePath modulePath) {
    super(RESULT_TYPE, modulePath, IF_FUNCTION_NAME, createParameters(), internal());
  }

  private static ImmutableList<Item> createParameters() {
    return list(
        parameter(bool(), "condition"),
        parameter(RESULT_TYPE, "then"),
        parameter(RESULT_TYPE, "else"));
  }

  private static Item parameter(Type type, String name) {
    return new Item(type, name, Optional.empty());
  }
}
