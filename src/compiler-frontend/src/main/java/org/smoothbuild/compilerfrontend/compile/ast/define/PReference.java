package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public final class PReference implements PPolymorphic {
  private final String nameText;
  private final Location location;
  private Id id;
  private Referenceable referenced;
  private SSchema sSchema;

  public PReference(String nameText, Location location) {
    this.nameText = nameText;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public Id id() {
    return id;
  }

  public Referenceable referenced() {
    return Objects.requireNonNull(referenced);
  }

  public void setReferenced(Referenceable referenced) {
    this.referenced = referenced;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public SSchema schema() {
    return sSchema;
  }

  public void setSSchema(SSchema schema) {
    this.sSchema = schema;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PReference that
        && Objects.equals(this.id(), that.id())
        && Objects.equals(this.referenced(), that.referenced())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id(), referenced(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PReference")
        .addField("name", id())
        .addField("location", location())
        .toString();
  }
}
