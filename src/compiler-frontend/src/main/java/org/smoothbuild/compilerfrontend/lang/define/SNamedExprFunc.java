package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Named Expression Function (function that has a body and a name).
 * This class is immutable.
 */
public final class SNamedExprFunc extends SNamedFunc implements SExprFunc {
  private final SExpr body;

  public SNamedExprFunc(
      SFuncSchema schema, Fqn fqn, NList<SItem> params, SExpr body, Location location) {
    super(schema, fqn, params, location);
    this.body = body;
  }

  @Override
  public SExpr body() {
    return body;
  }

  @Override
  public String toSourceCode() {
    var localTypeVars = schema().typeParams().toSet();
    return funcHeaderToSourceCode(localTypeVars) + "\n  = " + body.toSourceCode(localTypeVars)
        + ";";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SNamedExprFunc that
        && this.schema().equals(that.schema())
        && this.fqn().equals(that.fqn())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), fqn(), params(), body, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SNamedExprFunc")
        .addField("fqn", fqn())
        .addField("schema", schema())
        .addListField("params", params().list())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }
}
