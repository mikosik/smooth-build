package org.smoothbuild.lang.base.define;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.define.SModule.calculateModuleHash;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableMap;

public class InternalModule {

  public static SModule internalModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = BASE_TYPES.stream()
        .collect(toImmutableMap(Type::name, t -> new BaseTypeDefinition(modulePath, t)));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new SModule(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private static ImmutableMap<String, Referencable> referencables(ModulePath modulePath) {
    Function ifFunction = new IfFunction(modulePath);
    return ImmutableMap.of(ifFunction.name(), ifFunction);
  }
}
