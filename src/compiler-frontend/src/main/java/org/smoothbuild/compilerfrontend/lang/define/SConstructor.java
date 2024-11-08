package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Structure constructor.
 * This class is immutable.
 */
public final class SConstructor extends SNamedFunc {
  public SConstructor(SFuncSchema schema, String name, NList<SItem> params, Location location) {
    super(schema, name, params, location);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SConstructor that
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), name(), params(), location());
  }

  @Override
  public String toString() {
    var fields = list("name = " + name(), fieldsToString()).toString("\n");
    return "SConstructor(\n" + indent(fields) + "\n)";
  }
}
