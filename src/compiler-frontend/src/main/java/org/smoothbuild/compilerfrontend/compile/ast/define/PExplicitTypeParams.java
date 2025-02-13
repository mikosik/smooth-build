package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.STypeVarSet;

public final class PExplicitTypeParams implements PTypeParams {
  private final List<PTypeParam> typeVars;
  private final Location location;

  public PExplicitTypeParams(List<PTypeParam> typeVars, Location location) {
    this.typeVars = typeVars;
    this.location = location;
  }

  public List<PTypeParam> typeVars() {
    return typeVars;
  }

  public Location location() {
    return location;
  }

  @Override
  public STypeVarSet toTypeVarSet() {
    return sTypeVarSet(typeVars.map(PTypeParam::type));
  }
}
