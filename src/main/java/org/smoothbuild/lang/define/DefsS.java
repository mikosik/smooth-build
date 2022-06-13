package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.util.collect.NList;

public record DefsS(
    NList<TDefS> tDefs,
    NList<TopRefableS> topRefables) {

  public static DefsS empty() {
    return new DefsS(nList(), nList());
  }

  public DefsS withModule(ModS mod) {
    return new DefsS(
        nList(concat(tDefs, mod.tDefs())),
        nList(concat(topRefables, mod.topRefables()))
    );
  }
}
