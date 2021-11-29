package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableMap;

public record DefinitionsS(
    ImmutableMap<ModPath, ModS> modules,
    NList<DefinedType> types,
    NList<TopEvalS> referencables) {

  public static DefinitionsS empty() {
    return new DefinitionsS(ImmutableMap.of(), nList(), nList());
  }

  public DefinitionsS withModule(ModS module) {
    return new DefinitionsS(
        concat(modules, module),
        nList(Lists.concat(types, module.types())),
        nList(Lists.concat(referencables, module.referencables()))
    );
  }

  private static ImmutableMap<ModPath, ModS> concat(
      ImmutableMap<ModPath, ModS> modules, ModS module) {
    var builder = ImmutableMap.<ModPath, ModS>builder();
    builder.putAll(modules);
    builder.put(module.path(), module);
    return builder.build();
  }
}
