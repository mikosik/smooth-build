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
 * Named Expression Function (function that has a body and a name).
 * This class is immutable.
 */
public final class SNamedExprFunc extends SNamedFunc implements SExprFunc {
  private final SExpr body;

  public SNamedExprFunc(
      SType resultType, Fqn fqn, NList<SItem> params, SExpr body, Location location) {
    super(resultType, fqn, params, location);
    this.body = body;
  }

  @Override
  public SExpr body() {
    return body;
  }

  @Override
  public String toSourceCode() {
    return toSourceCode(none());
  }

  @Override
  public String toSourceCode(Maybe<List<STypeVar>> typeParams) {
    return funcHeaderToSourceCode(typeParams) + "\n  = " + body.toSourceCode() + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SNamedExprFunc that
        && this.type().equals(that.type())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), fqn(), params(), body, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SNamedExprFunc")
        .addField("fqn", fqn())
        .addField("type", type())
        .addListField("params", params().list())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }
}
