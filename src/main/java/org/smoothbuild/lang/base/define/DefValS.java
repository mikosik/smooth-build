package org.smoothbuild.lang.base.define;

import java.util.Objects;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.ExprS;

/**
 * This class is immutable.
 */
public final class DefValS extends TopEvalS {
  private final ExprS body;

  public DefValS(TypeS type, ModPath modPath, String name, ExprS body, Loc loc) {
    super(type, modPath, name, loc);
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


