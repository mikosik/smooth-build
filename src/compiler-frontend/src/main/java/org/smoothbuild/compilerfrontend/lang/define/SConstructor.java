package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncTypeScheme;

/**
 * Structure constructor.
 * This class is immutable.
 */
public final class SConstructor extends SNamedFunc {
  public SConstructor(
      SFuncTypeScheme funcTypeScheme, Fqn fqn, NList<SItem> params, Location location) {
    super(funcTypeScheme, fqn, params, location);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SConstructor that
        && this.typeScheme().equals(that.typeScheme())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeScheme(), fqn(), params(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SConstructor")
        .addField("fqn", fqn())
        .addField("typeScheme", this.typeScheme())
        .addListField("params", params().list())
        .addField("location", location())
        .toString();
  }

  @Override
  public String toSourceCode() {
    return funcHeaderToSourceCode() + "\n  = <generated>;";
  }
}
