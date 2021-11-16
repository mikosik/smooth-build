package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableMap;

public record DefinitionsS(
    ImmutableMap<ModulePath, ModuleS> modules,
    NList<DefinedType> types,
    NList<TopEvaluableS> referencables) {

  public static DefinitionsS empty() {
    return new DefinitionsS(ImmutableMap.of(), nList(), nList());
  }

  public DefinitionsS withModule(ModuleS module) {
    return new DefinitionsS(
        concat(modules, module),
        nList(Lists.concat(types, module.types())),
        nList(Lists.concat(referencables, module.referencables()))
    );
  }

  private static ImmutableMap<ModulePath, ModuleS> concat(
      ImmutableMap<ModulePath, ModuleS> modules, ModuleS module) {
    var builder = ImmutableMap.<ModulePath, ModuleS>builder();
    builder.putAll(modules);
    builder.put(module.path(), module);
    return builder.build();
  }
}
