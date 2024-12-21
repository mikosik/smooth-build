package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public final class PReference extends PPolymorphic {
  private SSchema sSchema;

  public PReference(String nameText, Location location) {
    super(nameText, location);
  }

  @Override
  public SSchema sSchema() {
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
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PReference")
        .addField("name", id())
        .addField("location", location())
        .toString();
  }
}
