package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Anonymous function.
 * This class is immutable.
 */
public final class AnonFuncS implements FuncS, MonoizableS {
  private final FuncSchemaS schema;
  private final NList<ItemS> params;
  private final ExprS body;
  private final Loc loc;

  public AnonFuncS(FuncSchemaS schema, NList<ItemS> params, ExprS body, Loc loc) {
    this.schema = schema;
    this.params = params;
    this.body = body;
    this.loc = loc;
  }


  @Override
  public FuncSchemaS schema() {
    return schema;
  }

  @Override
  public NList<ItemS> params() {
    return params;
  }

  public ExprS body() {
    return body;
  }

  @Override
  public Loc loc() {
    return loc;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnonFuncS that
        && this.schema.equals(that.schema)
        && this.params.equals(that.params)
        && this.body.equals(that.body)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, params, body, loc());
  }

  @Override
  public String toString() {
    var fields = fieldsToString() + "\nbody = " + body;
    return "AnonFuncS(\n" + indent(fields) + "\n)";
  }
}