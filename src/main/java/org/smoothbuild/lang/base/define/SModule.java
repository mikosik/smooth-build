package org.smoothbuild.lang.base.define;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public record SModule(
    ModulePath path,
    Hash hash,
    ModuleFiles files,
    ImmutableList<SModule> referencedModules,
    ImmutableMap<String, ? extends Defined> types,
    ImmutableMap<String, ? extends Referencable> referencables) {

  public static SModule baseTypesModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = BASE_TYPES.stream()
        .collect(toImmutableMap(Type::name, t -> new BaseTypeDefinition(modulePath, t)));
    Hash hash = moduleHash(modulePath, Hash.of(new Hash[] {}), list());
    return new SModule(modulePath, hash, null, list(), types, ImmutableMap.of());
  }

  public static Hash moduleHash(ModulePath path, Hash filesHash, ImmutableList<SModule> modules) {
    return Hash.of(
        Hash.of(path.toString()),
        filesHash,
        referencedModulesHash(modules)
    );
  }

  private static Hash referencedModulesHash(ImmutableList<SModule> modules) {
    Hash[] hashes = modules.stream()
        .map(SModule::hash)
        .toArray(Hash[]::new);
    return Hash.of(hashes);
  }
}
