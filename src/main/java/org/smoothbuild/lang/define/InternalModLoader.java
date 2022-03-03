package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.lang.type.impl.TypeFS;

public class InternalModLoader {
  private final TypeFS typeFS;

  @Inject
  public InternalModLoader(TypeFS typeFS) {
    this.typeFS = typeFS;
  }

  public ModS load() {
    ModPath path = new ModPath("internal-module");
    var types = nList(map(typeFS.baseTs(), t -> (DefTypeS) new DefBaseTypeS(path, t)));
    return new ModS(path, null, types, nList());
  }
}
