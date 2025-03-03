package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.Maybe.none;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Structure constructor.
 * This class is immutable.
 */
public final class SConstructor extends SNamedFunc {
  public SConstructor(SType resultType, Fqn fqn, NList<SItem> params, Location location) {
    super(resultType, fqn, params, location);
  }

  @Override
  public String toSourceCode() {
    return toSourceCode(none());
  }

  @Override
  public String toSourceCode(Maybe<List<STypeVar>> typeParams) {
    return funcHeaderToSourceCode(typeParams) + "\n  = <generated>;";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SConstructor that
        && this.type().equals(that.type())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), fqn(), params(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SConstructor")
        .addField("fqn", fqn())
        .addField("type", type())
        .addListField("params", params().list())
        .addField("location", location())
        .toString();
  }
}
