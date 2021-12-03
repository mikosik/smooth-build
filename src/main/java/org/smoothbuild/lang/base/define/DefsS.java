package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.util.collect.NList;

public record DefsS(
    NList<DefTypeS> types,
    NList<TopEvalS> topEvals) {

  public static DefsS empty() {
    return new DefsS(nList(), nList());
  }

  public DefsS withModule(ModS mod) {
    return new DefsS(
        nList(concat(types, mod.types())),
        nList(concat(topEvals, mod.topEvals()))
    );
  }
}
