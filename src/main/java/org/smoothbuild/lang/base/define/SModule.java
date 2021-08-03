package org.smoothbuild.lang.base.define;

import static java.util.Arrays.asList;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public record SModule(
    ModulePath path,
    Hash hash,
    ModuleFiles files,
    ImmutableList<SModule> referencedModules,
    ImmutableMap<String, ? extends Defined> types,
    ImmutableMap<String, ? extends GlobalReferencable> referencables) {

  public static Hash calculateModuleHash(ModulePath path, Hash filesHash, List<SModule> modules) {
    return Hash.of(asList(
        Hash.of(path.toString()),
        filesHash,
        referencedModulesHash(modules)));
  }

  private static Hash referencedModulesHash(List<SModule> modules) {
    return Hash.of(map(modules, SModule::hash));
  }
}
