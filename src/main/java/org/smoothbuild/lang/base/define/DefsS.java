package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.NList.nList;

import java.util.Map;

import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableMap;

public record DefsS(
    ImmutableMap<ModPath, ModS> modules,
    NList<DefTypeS> types,
    NList<TopEvalS> topEvals) {

  public static DefsS empty() {
    return new DefsS(ImmutableMap.of(), nList(), nList());
  }

  public DefsS withModule(ModS mod) {
    return new DefsS(
        concat(modules, mod),
        nList(Lists.concat(types, mod.types())),
        nList(Lists.concat(topEvals, mod.topEvals()))
    );
  }

  private static ImmutableMap<ModPath, ModS> concat(Map<ModPath, ModS> mods, ModS mod) {
    var builder = ImmutableMap.<ModPath, ModS>builder();
    builder.putAll(mods);
    builder.put(mod.path(), mod);
    return builder.build();
  }
}
