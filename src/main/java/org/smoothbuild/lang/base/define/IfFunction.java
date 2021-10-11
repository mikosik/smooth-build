package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

public class IfFunction extends Function {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFunction(ModulePath modulePath, Typing typing) {
    this(typing.variable("A"), typing.bool(), modulePath, typing);
  }

  private IfFunction(Type resultType, Type boolType, ModulePath modulePath, Typing typing) {
    this(resultType, createParameters(resultType, boolType, modulePath), modulePath, typing);
  }

  private IfFunction(Type resultType, ImmutableList<Item> parameters, ModulePath modulePath,
      Typing typing) {
    super(typing.function(resultType, toTypes(parameters)),
        modulePath, IF_FUNCTION_NAME, parameters, internal());
  }

  private static ImmutableList<Item> createParameters(
      Type resultType, Type boolType, ModulePath modulePath) {
    return list(
        parameter(boolType, modulePath, "condition"),
        parameter(resultType, modulePath, "then"),
        parameter(resultType, modulePath, "else"));
  }

  public static Item parameter(Type type, ModulePath modulePath, String name) {
    return new Item(type, modulePath, name, Optional.empty(), internal());
  }
}
