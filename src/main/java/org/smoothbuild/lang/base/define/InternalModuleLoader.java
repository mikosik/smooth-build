package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.util.collect.NList;

public class InternalModuleLoader {
  private final TypeFactoryS factory;

  @Inject
  public InternalModuleLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public ModuleS loadModule() {
    ModulePath path = new ModulePath("internal-module");
    var types = nList(map(factory.baseTypes(), t -> (DefinedType) new DefinedBaseType(path, t)));
    return new ModuleS(path, null, list(), types, evaluables(path));
  }

  private NList<TopEvalS> evaluables(ModulePath modulePath) {
    var ifFunction = new IfFunctionS(modulePath, factory);
    var mapFunction = new MapFunctionS(modulePath, factory);
    var trueValue = new BoolValueS(true, modulePath, factory);
    var falseValue = new BoolValueS(false, modulePath, factory);
    return nList(ifFunction, mapFunction, trueValue, falseValue);
  }
}
