package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Named Expression Function (function that has a body and a name).
 * This class is immutable.
 */
public final class SNamedExprFunc extends SNamedFunc implements SExprFunc {
  private final SExpr body;

  public SNamedExprFunc(
      SFuncSchema schema, Id id, NList<SItem> params, SExpr body, Location location) {
    super(schema, id, params, location);
    this.body = body;
  }

  @Override
  public SExpr body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SNamedExprFunc that
        && this.schema().equals(that.schema())
        && this.id().equals(that.id())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), id(), params(), body, location());
  }

  @Override
  public String toString() {
    var fields = list("name = " + id(), fieldsToString(), "body = " + body).toString("\n");
    return "SNamedExprFunc(\n" + indent(fields) + "\n)";
  }
}
