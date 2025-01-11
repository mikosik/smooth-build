package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public final class PTypeParam implements PTypeDefinition {
  private final String nameText;
  private final Location location;
  private Fqn fqn;

  public PTypeParam(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public SType type() {
    return new SVar(fqn);
  }

  @Override
  public Location location() {
    return location;
  }
}
