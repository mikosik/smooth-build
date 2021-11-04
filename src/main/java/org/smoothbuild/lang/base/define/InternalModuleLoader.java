package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.SModule.calculateModuleHash;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.toMap;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.BaseSType;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;

import com.google.common.collect.ImmutableMap;

public class InternalModuleLoader {
  private final TypeFactoryS factory;

  @Inject
  public InternalModuleLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public SModule loadModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = toMap(factory.baseTypes(),
        Type::name, t -> new DefinedBaseType(modulePath, (BaseSType) t));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new SModule(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private ImmutableMap<String, GlobalReferencable> referencables(ModulePath modulePath) {
    Function ifFunction = new IfFunction(modulePath, factory);
    Function mapFunction = new MapFunction(modulePath, factory);
    return toMap(list(ifFunction, mapFunction), Defined::name, f -> f);
  }
}
