package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.ModuleS.calculateModuleHash;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.toMap;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;

import com.google.common.collect.ImmutableMap;

public class InternalModuleLoader {
  private final TypeFactoryS factory;

  @Inject
  public InternalModuleLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public ModuleS loadModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = toMap(factory.baseTypes(),
        Type::name, t -> new DefinedBaseType(modulePath, t));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new ModuleS(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private ImmutableMap<String, GlobalReferencable> referencables(ModulePath modulePath) {
    FunctionS ifFunction = new IfFunctionS(modulePath, factory);
    FunctionS mapFunction = new MapFunctionS(modulePath, factory);
    return toMap(list(ifFunction, mapFunction), Defined::name, f -> f);
  }
}
