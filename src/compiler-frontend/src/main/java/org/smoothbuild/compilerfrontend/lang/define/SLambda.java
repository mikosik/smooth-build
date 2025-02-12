package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Lambda.
 *
 * This class is immutable.
 */
public final class SLambda implements SExprFunc, SPolymorphic {
  private final SFuncSchema schema;
  private final Fqn fqn;
  private final NList<SItem> params;
  private final SExpr body;
  private final Location location;

  public SLambda(SFuncSchema schema, Fqn fqn, NList<SItem> params, SExpr body, Location location) {
    this.schema = schema;
    this.fqn = fqn;
    this.params = params;
    this.body = body;
    this.location = location;
  }

  @Override
  public SFuncSchema schema() {
    return schema;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public NList<SItem> params() {
    return params;
  }

  @Override
  public SExpr body() {
    return body;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public String toSourceCode() {
    var localVars = schema().typeParams();
    return localVars.toSourceCode()
        + params().list().map(sItem -> sItem.toSourceCode(localVars)).toString("(", ", ", ")")
        + " -> " + body().toSourceCode(localVars);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SLambda that
        && this.schema.equals(that.schema)
        && this.params.equals(that.params)
        && this.body.equals(that.body)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, params, body, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SLambda")
        .addField("fqn", fqn())
        .addField("schema", schema())
        .addListField("params", params().list())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }
}
