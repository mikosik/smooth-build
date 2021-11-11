package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;

public class IfFunction extends FunctionS {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFunction(ModulePath modulePath, TypeFactoryS factory) {
    this(factory.variable("A"), factory.bool(), modulePath, factory);
  }

  private IfFunction(
      TypeS resultType, TypeS boolType, ModulePath modulePath, TypeFactoryS factory) {
    this(resultType, createParameters(resultType, boolType, modulePath), modulePath, factory);
  }

  private IfFunction(TypeS resultType, NamedList<Item> parameters, ModulePath modulePath,
      TypeFactoryS factory) {
    super(factory.function(resultType, map(parameters, Defined::type)),
        modulePath, IF_FUNCTION_NAME, parameters, internal());
  }

  private static NamedList<Item> createParameters(
      TypeS resultType, TypeS boolType, ModulePath modulePath) {
    return namedList(list(
        parameter(boolType, modulePath, "condition"),
        parameter(resultType, modulePath, "then"),
        parameter(resultType, modulePath, "else")));
  }

  public static Item parameter(TypeS type, ModulePath modulePath, String name) {
    return new Item(type, modulePath, name, Optional.empty(), internal());
  }
}
