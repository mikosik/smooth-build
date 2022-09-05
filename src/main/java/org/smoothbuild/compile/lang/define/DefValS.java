package org.smoothbuild.compile.lang.define;

import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

/**
 * Defined value (one that has a body).
 * This class is immutable.
 */
public final class DefValS extends NamedValS {
  private final ExprS body;

  public DefValS(TypeS type, ModPath modPath, String name, ExprS body, Loc loc) {
    super(type, modPath, name, loc);
    this.body = body;
  }

  public ExprS body() {
    return body;
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new DefValS(type().mapVars(mapper), modPath(), name(),
        body.mapVars(b -> b.mapVars(mapper)), loc());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefValS that
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.body().equals(that.body())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), body(), loc());
  }

  @Override
  public String toString() {
    return "DefVal(`" + type().name() + " " + name() + " = " + body + "`)";
  }
}


