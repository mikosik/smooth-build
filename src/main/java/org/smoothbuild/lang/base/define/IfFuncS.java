package org.smoothbuild.lang.base.define;

import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

public final class IfFuncS extends FuncS {
  public static final String IF_FUNCTION_NAME = "if";

  public IfFuncS(ModPath modPath, TypeFactoryS factory) {
    this(factory.variable("A"), factory.bool(), modPath, factory);
  }

  private IfFuncS(
      TypeS resultType, TypeS boolType, ModPath modPath, TypeFactoryS factory) {
    this(resultType, createParams(resultType, boolType, modPath), modPath, factory);
  }

  private IfFuncS(TypeS resultType, NList<Item> params, ModPath modPath,
      TypeFactoryS factory) {
    super(factory.abstFunc(resultType, map(params, Defined::type)), modPath, IF_FUNCTION_NAME, params, internal());
  }

  private static NList<Item> createParams(TypeS resultType, TypeS boolType, ModPath modPath) {
    return nList(
        new Item(boolType, modPath, "condition", empty(), internal()),
        new Item(resultType, modPath, "then", empty(), internal()),
        new Item(resultType, modPath, "else", empty(), internal()));
  }
}
