package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
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
    var fields = list("name = " + id(), "location = " + location()).toString("\n");
    return "PReference(\n" + indent(fields) + "\n)";
  }
}
