package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.lang.base.type.impl.TypeSF;

public class InternalModLoader {
  private final TypeSF typeSF;

  @Inject
  public InternalModLoader(TypeSF typeSF) {
    this.typeSF = typeSF;
  }

  public ModS load() {
    ModPath path = new ModPath("internal-module");
    var types = nList(map(typeSF.baseTs(), t -> (DefTypeS) new DefBaseTypeS(path, t)));
    return new ModS(path, null, types, nList());
  }
}
