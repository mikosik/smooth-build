package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<ModulePath, ModuleS> modules,
    NamedList<DefinedType> types,
    NamedList<GlobalReferencable> referencables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), NamedList.empty(), NamedList.empty());
  }

  public Definitions withModule(ModuleS module) {
    return new Definitions(
        concat(modules, module),
        namedList(Lists.concat(types, module.types())),
        namedList(Lists.concat(referencables, module.referencables()))
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
