package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;

/**
 * Lambda.
 *
 * This class is immutable.
 */
public final class SLambda implements SExprFunc, SExpr {
  private final SFuncSchema schema;
  private final Fqn fqn;
  private final NList<SItem> params;
  private final SExpr body;
  private final Location location;

  public SLambda(SFuncType type, Fqn fqn, NList<SItem> params, SExpr body, Location location) {
    this(new SFuncSchema(list(), type), fqn, params, body, location);
  }

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
  public SFuncType evaluationType() {
    return schema.type();
  }

  @Override
  public String toSourceCode() {
    return params().list().map(SItem::toSourceCode).toString("(", ", ", ")") + " -> "
        + body().toSourceCode();
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
        .addField("type", schema().type())
        .addListField("params", params().list())
        .addField("location", location())
        .addField("body", body)
        .toString();
  }
}
