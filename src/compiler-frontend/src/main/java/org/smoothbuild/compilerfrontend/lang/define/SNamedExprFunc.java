package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.NList;
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
  public String toSourceCode() {
    return funcHeaderToSourceCode() + "\n  = " + body.toSourceCode() + ";";
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
    return new ToStringBuilder("SNamedExprFunc")
        .addField("name", id())
        .addField("schema", schema())
        .addListField("params", params().list())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }
}
