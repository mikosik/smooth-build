package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public final class PReference implements HasLocation {
  private final String nameText;
  private final Location location;
  private @Nullable Fqn fqn;
  private @Nullable Referenceable referenced;

  public PReference(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  public Fqn fqn() {
    return checkInitializedToNotNull(fqn, "fqn");
  }

  public Referenceable referenced() {
    return checkInitializedToNotNull(referenced, "referenced");
  }

  public void setReferenced(Referenceable referenced) {
    this.referenced = referenced;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PReference that
        && Objects.equals(this.fqn, that.fqn)
        && Objects.equals(this.referenced, that.referenced)
        && Objects.equals(this.location, that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fqn, referenced, location);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PReference")
        .addField("fqn", fqn())
        .addField("location", location())
        .toString();
  }
}
