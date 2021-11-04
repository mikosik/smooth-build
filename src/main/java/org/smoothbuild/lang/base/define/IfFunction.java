package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Optional;

import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.impl.FunctionSType;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

public class IfFunction extends Function {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFunction(ModulePath modulePath, TypeFactory typing) {
    this((TypeS) typing.variable("A"), (TypeS) typing.bool(), modulePath, typing);
  }

  private IfFunction(TypeS resultType, TypeS boolType, ModulePath modulePath, TypeFactory typing) {
    this(resultType, createParameters(resultType, boolType, modulePath), modulePath, typing);
  }

  private IfFunction(TypeS resultType, ImmutableList<Item> parameters, ModulePath modulePath,
      TypeFactory typing) {
    super((FunctionSType) typing.function(resultType, toTypes(parameters)),
        modulePath, IF_FUNCTION_NAME, parameters, internal());
  }

  private static ImmutableList<Item> createParameters(
      TypeS resultType, TypeS boolType, ModulePath modulePath) {
    return list(
        parameter(boolType, modulePath, "condition"),
        parameter(resultType, modulePath, "then"),
        parameter(resultType, modulePath, "else"));
  }

  public static Item parameter(TypeS type, ModulePath modulePath, String name) {
    return new Item(type, modulePath, name, Optional.empty(), internal());
  }
}
