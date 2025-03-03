package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.compilerfrontend.lang.define.SNamedValue.valueHeaderToSourceCode;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Named Expression Value (one that has a body).
 * This class is immutable.
 */
public final class SNamedExprValue implements SNamedValue, IdentifiableCode {
  private final SExpr body;
  private final SType type;
  private final Fqn fqn;
  private final Location location;

  public SNamedExprValue(SType type, Fqn fqn, SExpr body, Location location) {
    this.type = type;
    this.fqn = fqn;
    this.body = body;
    this.location = location;
  }

  public SExpr body() {
    return body;
  }

  @Override
  public String toSourceCode() {
    return toSourceCode(none());
  }

  @Override
  public String toSourceCode(Maybe<List<STypeVar>> typeParams) {
    return valueHeaderToSourceCode(this, typeParams) + "\n  = " + body.toSourceCode() + ";";
  }

  @Override
  public SType type() {
    return type;
  }

  @Override
  public Fqn fqn() {
    return fqn;
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
    return object instanceof SNamedExprValue that
        && this.type.equals(that.type)
        && this.fqn.equals(that.fqn)
        && this.body.equals(that.body)
        && this.location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, fqn, body, location);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SNamedExprValue")
        .addField("type", type)
        .addField("fqn", fqn)
        .addField("location", location)
        .addField("body", body)
        .toString();
  }
}
