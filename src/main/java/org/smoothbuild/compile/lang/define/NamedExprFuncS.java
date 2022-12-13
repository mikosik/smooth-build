package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Named Expression Function (function that has a body and a name).
 * This class is immutable.
 */
public final class NamedExprFuncS extends NamedFuncS implements ExprFuncS {
  private final ExprS body;

  public NamedExprFuncS(FuncSchemaS schema, String name, NList<ItemS> params, ExprS body, Loc loc) {
    super(schema, name, params, loc);
    this.body = body;
  }

  @Override
  public ExprS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NamedExprFuncS that
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), name(), params(), body, loc());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "name = " + name(),
        fieldsToString(),
        "body = " + body);
    return "NamedExprFuncS(\n" + indent(fields) + "\n)";
  }
}
