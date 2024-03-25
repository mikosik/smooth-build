package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * Lambda.
 *
 * This class is immutable.
 */
public final class SLambda implements SExprFunc, SPolymorphic {
  private final SFuncSchema schema;
  private final NList<SItem> params;
  private final SExpr body;
  private final Location location;

  public SLambda(SFuncSchema schema, NList<SItem> params, SExpr body, Location location) {
    this.schema = schema;
    this.params = params;
    this.body = body;
    this.location = location;
  }

  @Override
  public SFuncSchema schema() {
    return schema;
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
    var fields = fieldsToString() + "\nbody = " + body;
    return "SLambda(\n" + indent(fields) + "\n)";
  }
}
