package org.smoothbuild.lang.base.define;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;

public class IfFunctionS extends FunctionS {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFunctionS(ModulePath modulePath, TypeFactoryS factory) {
    this(factory.variable("A"), factory.bool(), modulePath, factory);
  }

  private IfFunctionS(
      TypeS resultType, TypeS boolType, ModulePath modulePath, TypeFactoryS factory) {
    this(resultType, createParameters(resultType, boolType, modulePath), modulePath, factory);
  }

  private IfFunctionS(TypeS resultType, NamedList<Item> parameters, ModulePath modulePath,
      TypeFactoryS factory) {
    super(factory.function(resultType, map(parameters, Defined::type)),
        modulePath, IF_FUNCTION_NAME, parameters, internal());
  }

  private static NamedList<Item> createParameters(
      TypeS resultType, TypeS boolType, ModulePath modulePath) {
    return namedList(list(
        new Item(boolType, modulePath, "condition", empty(), internal()),
        new Item(resultType, modulePath, "then", empty(), internal()),
        new Item(resultType, modulePath, "else", empty(), internal())));
  }
}
