package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.util.collect.NList;

/**
 * Defined function (function that has body).
 * This class is immutable.
 */
public final class DefFuncS extends FuncS {
  private final ExprS body;

  public DefFuncS(FuncTS type, String name, NList<ItemS> params, ExprS body, Loc loc) {
    super(type, name, params, loc);
    this.body = body;
  }

  public ExprS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefFuncS that
        && this.resT().equals(that.resT())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.body.equals(that.body)
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resT(), name(), params(), body, loc());
  }

  @Override
  public String toString() {
    var fields = funcFieldsToString() + "\nbody = " + body;
    return "DefFuncS(\n" + indent(fields) + "\n)";
  }
}
