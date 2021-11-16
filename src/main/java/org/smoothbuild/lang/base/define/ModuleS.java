package org.smoothbuild.lang.base.define;

import static java.util.Arrays.asList;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public record ModuleS(
    ModulePath path,
    Hash hash,
    ModuleFiles files,
    ImmutableList<ModuleS> referencedModules,
    NList<DefinedType> types,
    NList<TopEvaluableS> referencables) {

  public static Hash calculateModuleHash(ModulePath path, Hash filesHash, List<ModuleS> modules) {
    return Hash.of(asList(
        Hash.of(path.toString()),
        filesHash,
        referencedModulesHash(modules)));
  }

  private static Hash referencedModulesHash(List<ModuleS> modules) {
    return Hash.of(map(modules, ModuleS::hash));
  }
}
