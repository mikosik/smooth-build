package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Anonymous function.
 * This class is immutable.
 */
public final class AnonymousFuncS implements ExprFuncS, PolymorphicS {
  private final FuncSchemaS schema;
  private final NList<ItemS> params;
  private final ExprS body;
  private final Location location;

  public AnonymousFuncS(FuncSchemaS schema, NList<ItemS> params, ExprS body, Location location) {
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
    return object instanceof AnonymousFuncS that
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
    return "AnonymousFuncS(\n" + indent(fields) + "\n)";
  }
}
