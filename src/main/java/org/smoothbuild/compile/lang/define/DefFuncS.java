package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * Defined function (function that has body).
 * This class is immutable.
 */
public final class DefFuncS extends NamedFuncS implements ExprFuncS {
  private final ExprS body;

  public DefFuncS(FuncSchemaS schema, String name, NList<ItemS> params, ExprS body, Loc loc) {
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
    return object instanceof DefFuncS that
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
    return "DefFuncS(\n" + indent(fields) + "\n)";
  }
}
