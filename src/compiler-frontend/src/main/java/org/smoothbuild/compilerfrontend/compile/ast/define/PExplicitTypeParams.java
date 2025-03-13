package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public final class PExplicitTypeParams implements PTypeParams {
  private final List<PTypeParam> typeVars;
  private final Location location;

  public PExplicitTypeParams(List<PTypeParam> typeVars, Location location) {
    this.typeVars = typeVars;
    this.location = location;
  }

  public List<PTypeParam> typeParams() {
    return typeVars;
  }

  public Location location() {
    return location;
  }

  @Override
  public List<STypeVar> typeVars() {
    return typeVars.map(PTypeParam::type);
  }

  public String q() {
    return Strings.q(toSourceCode());
  }

  public String toSourceCode() {
    return typeParamsToSourceCode(typeVars.map(PTypeParam::type));
  }
}
