package org.smoothbuild.lang.base.define;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public final class IfFuncS extends FuncS {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFuncS(ModulePath modulePath, TypeFactoryS factory) {
    this(factory.variable("A"), factory.bool(), modulePath, factory);
  }

  private IfFuncS(
      TypeS resultType, TypeS boolType, ModulePath modulePath, TypeFactoryS factory) {
    this(resultType, createParams(resultType, boolType, modulePath), modulePath, factory);
  }

  private IfFuncS(TypeS resultType, NList<Item> params, ModulePath modulePath,
      TypeFactoryS factory) {
    super(factory.func(resultType, map(params, Defined::type)),
        modulePath, IF_FUNCTION_NAME, params, internal());
  }

  private static NList<Item> createParams(TypeS resultType, TypeS boolType, ModulePath modulePath) {
    return nList(
        new Item(boolType, modulePath, "condition", empty(), internal()),
        new Item(resultType, modulePath, "then", empty(), internal()),
        new Item(resultType, modulePath, "else", empty(), internal()));
  }
}
