package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.lang.type.BaseTS;
import org.smoothbuild.lang.type.TypeSF;

public class InternalModLoader {
  private final TypeSF typeSF;

  @Inject
  public InternalModLoader(TypeSF typeSF) {
    this.typeSF = typeSF;
  }

  public ModS load() {
    var modPath = new ModPath("internal-module");
    var types = nList(map(typeSF.baseTs(), t -> baseTDef(modPath, t)));
    return new ModS(modPath, null, types, nList());
  }

  private TDefS baseTDef(ModPath modPath, BaseTS baseTS) {
    return new BaseTDefS(modPath, baseTS);
  }
}
