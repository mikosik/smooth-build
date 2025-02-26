package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.TypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public final class PTypeReference extends PExplicitType {
  private Fqn fqn;
  private TypeDefinition referenced;

  public PTypeReference(String idText, Location location) {
    super(idText, location);
  }

  public Fqn fqn() {
    return fqn;
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  public TypeDefinition referenced() {
    return Objects.requireNonNull(referenced);
  }

  public void setReferenced(TypeDefinition referenced) {
    this.referenced = referenced;
  }
}
