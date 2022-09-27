package org.smoothbuild.compile.lang.define;

import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

/**
 * Unnamed Defined value (one that has a body).
 * This class is immutable.
 */
public final class UnnamedDefValS extends EvaluableS {
  private final ExprS body;

  public UnnamedDefValS(TypeS type, ExprS body, Loc loc) {
    super(type, loc);
    this.body = body;
  }

  public ExprS body() {
    return body;
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new UnnamedDefValS(type().mapVars(mapper), body.mapVars(b -> b.mapVars(mapper)), loc());
  }

  @Override
  public String label() {
    return "<unnamed>";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof UnnamedDefValS that
        && this.type().equals(that.type())
        && this.body().equals(that.body())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), body(), loc());
  }

  @Override
  public String toString() {
    return "DefVal(`" + type().name() + " = " + body + "`)";
  }
}
