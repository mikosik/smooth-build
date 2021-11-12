package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.ModuleS.calculateModuleHash;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NamedList.namedList;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.util.collect.NamedList;

public class InternalModuleLoader {
  private final TypeFactoryS factory;

  @Inject
  public InternalModuleLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public ModuleS loadModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = namedList(map(factory.baseTypes(), t -> new DefinedBaseType(modulePath, t)));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new ModuleS(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private NamedList<GlobalReferencable> referencables(ModulePath modulePath) {
    FunctionS ifFunction = new IfFunctionS(modulePath, factory);
    FunctionS mapFunction = new MapFunctionS(modulePath, factory);
    return namedList(list(ifFunction, mapFunction));
  }
}
