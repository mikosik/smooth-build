package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public final class PTypeReference extends PExplicitType {
  private @Nullable Fqn fqn;
  private @Nullable TypeDefinition referenced;

  public PTypeReference(String idText, Location location) {
    super(idText, location);
  }

  public Fqn fqn() {
    return checkInitializedToNotNull(fqn, "fqn");
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  public TypeDefinition referenced() {
    return checkInitializedToNotNull(referenced, "referenced");
  }

  public void setReferenced(TypeDefinition referenced) {
    this.referenced = referenced;
  }
}
