package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.util.collect.NList;

public class InternalModLoader {
  private final TypeFactoryS factory;

  @Inject
  public InternalModLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public ModS load() {
    ModPath path = new ModPath("internal-module");
    var types = nList(map(factory.baseTypes(), t -> (DefinedType) new DefinedBaseType(path, t)));
    return new ModS(path, null, list(), types, evaluables(path));
  }

  private NList<TopEvalS> evaluables(ModPath modPath) {
    var ifFunc = new IfFuncS(modPath, factory);
    var mapFunc = new MapFuncS(modPath, factory);
    var trueValue = new BoolValS(true, modPath, factory);
    var falseValue = new BoolValS(false, modPath, factory);
    return nList(ifFunc, mapFunc, trueValue, falseValue);
  }
}
