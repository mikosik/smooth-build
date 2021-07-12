package org.smoothbuild.lang.base.define;

import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableMap<ModulePath, SModule> modules,
    ImmutableMap<String, Defined> types,
    ImmutableMap<String, Referencable> referencables) {

  public static Definitions empty() {
    return new Definitions(ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of());
  }

  public Definitions withModule(SModule module) {
    return new Definitions(
        concat(modules, module),
        ImmutableMap.<String, Defined>builder()
            .putAll(types)
            .putAll(module.types())
            .build(),
        ImmutableMap.<String, Referencable>builder()
            .putAll(referencables)
            .putAll(module.referencables())
            .build()
    );
  }

  private static ImmutableMap<ModulePath, SModule> concat(
      ImmutableMap<ModulePath, SModule> modules, SModule module) {
    var builder = ImmutableMap.<ModulePath, SModule>builder();
    builder.putAll(modules);
    builder.put(module.path(), module);
    return builder.build();
  }
}
