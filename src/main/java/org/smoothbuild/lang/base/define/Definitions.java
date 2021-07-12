package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.Lists.concat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public record Definitions(
    ImmutableList<SModule> modules,
    ImmutableMap<String, Defined> types,
    ImmutableMap<String, Referencable> referencables) {

  public static Definitions empty() {
    return new Definitions(ImmutableList.of(), ImmutableMap.of(), ImmutableMap.of());
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
}
