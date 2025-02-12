package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Structure constructor.
 * This class is immutable.
 */
public final class SConstructor extends SNamedFunc {
  public SConstructor(SFuncSchema schema, Fqn fqn, NList<SItem> params, Location location) {
    super(schema, fqn, params, location);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SConstructor that
        && this.schema().equals(that.schema())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), fqn(), params(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SConstructor")
        .addField("fqn", fqn())
        .addField("schema", schema())
        .addListField("params", params().list())
        .addField("location", location())
        .toString();
  }

  @Override
  public String toSourceCode() {
    return funcHeaderToSourceCode(schema().typeParams()) + "\n  = <generated>;";
  }
}
