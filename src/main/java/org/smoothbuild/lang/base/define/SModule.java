package org.smoothbuild.lang.base.define;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.asList;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

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
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new SModule(modulePath, hash, null, list(), types, ImmutableMap.of());
  }

  public static Hash calculateModuleHash(ModulePath path, Hash filesHash, ImmutableList<SModule> modules) {
    return Hash.of(asList(
        Hash.of(path.toString()),
        filesHash,
        referencedModulesHash(modules)));
  }

  private static Hash referencedModulesHash(ImmutableList<SModule> modules) {
    return Hash.of(map(modules, SModule::hash));
  }
}
