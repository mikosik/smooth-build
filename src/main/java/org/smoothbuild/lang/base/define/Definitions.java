package org.smoothbuild.lang.base.define;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<ModulePath, ModuleS> modules,
    ImmutableMap<String, Defined> types,
    ImmutableMap<String, GlobalReferencable> referencables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of());
  }

  public Definitions withModule(ModuleS module) {
    return new Definitions(
        concat(modules, module),
        ImmutableMap.<String, Defined>builder()
            .putAll(types)
            .putAll(module.types())
            .build(),
        ImmutableMap.<String, GlobalReferencable>builder()
            .putAll(referencables)
            .putAll(module.referencables())
            .build()
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
