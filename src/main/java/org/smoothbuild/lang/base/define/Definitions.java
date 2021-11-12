package org.smoothbuild.lang.base.define;

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
        NamedList.concat(types, module.types()),
        NamedList.concat(this.referencables, module.referencables())
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
