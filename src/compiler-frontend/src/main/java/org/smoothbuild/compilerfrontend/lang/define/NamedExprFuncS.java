package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.FuncSchemaS;

/**
 * Named Expression Function (function that has a body and a name).
 * This class is immutable.
 */
public final class NamedExprFuncS extends NamedFuncS implements ExprFuncS {
  private final ExprS body;

  public NamedExprFuncS(
      FuncSchemaS schema, String name, NList<ItemS> params, ExprS body, Location location) {
    super(schema, name, params, location);
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
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), name(), params(), body, location());
  }

  @Override
  public String toString() {
    var fields = list("name = " + name(), fieldsToString(), "body = " + body).toString("\n");
    return "NamedExprFuncS(\n" + indent(fields) + "\n)";
  }
}