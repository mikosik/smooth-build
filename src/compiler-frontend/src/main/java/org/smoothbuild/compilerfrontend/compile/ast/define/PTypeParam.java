package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public final class PTypeParam implements PTypeDefinition {
  private final String nameText;
  private final Location location;
  private Name name;

  public PTypeParam(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  public void setName(Name name) {
    this.name = name;
  }

  @Override
  public Name name() {
    return name;
  }

  @Override
  public STypeVar type() {
    return new STypeVar(name);
  }

  @Override
  public Location location() {
    return location;
  }
}
