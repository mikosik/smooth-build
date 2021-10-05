package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Typing;

import com.google.common.collect.ImmutableList;

public class IfFunction extends Function {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFunction(Typing typing, ModulePath modulePath) {
    this(typing.variable("A"), typing.boolT(), modulePath);
  }

  private IfFunction(Type resultType, Type boolType, ModulePath modulePath) {
    super(resultType, modulePath, IF_FUNCTION_NAME,
        createParameters(resultType, boolType, modulePath), internal());
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
