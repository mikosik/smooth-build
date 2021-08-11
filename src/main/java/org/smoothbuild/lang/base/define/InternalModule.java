package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.SModule.calculateModuleHash;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Maps.toMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableMap;

public class InternalModule {

  public static SModule internalModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = toMap(BASE_TYPES, Type::name, t -> new DefinedBaseType(modulePath, t));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new SModule(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private static ImmutableMap<String, GlobalReferencable> referencables(ModulePath modulePath) {
    Function ifFunction = new IfFunction(modulePath);
    Function mapFunction = new MapFunction(modulePath);
    return toMap(list(ifFunction, mapFunction), Defined::name, f -> f);
  }
}
