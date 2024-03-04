package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.FuncSchemaS;

/**
 * Lambda.
 *
 * This class is immutable.
 */
public final class LambdaS implements ExprFuncS, PolymorphicS {
  private final FuncSchemaS schema;
  private final NList<ItemS> params;
  private final ExprS body;
  private final Location location;

  public LambdaS(FuncSchemaS schema, NList<ItemS> params, ExprS body, Location location) {
    this.schema = schema;
    this.params = params;
    this.body = body;
    this.location = location;
  }

  @Override
  public FuncSchemaS schema() {
    return schema;
  }

  @Override
  public NList<ItemS> params() {
    return params;
  }

  @Override
  public ExprS body() {
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
    return object instanceof LambdaS that
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
    return "LambdaS(\n" + indent(fields) + "\n)";
  }
}
