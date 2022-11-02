package org.smoothbuild.compile.lang.define;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Tal;

/**
 * Unnamed Defined value (one that has a body).
 * This class is immutable.
 */
public final class UnnamedValS extends Tal implements EvaluableS {
  private final ExprS body;

  public UnnamedValS(ExprS body) {
    super(body.evalT(), body.loc());
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
    return object instanceof UnnamedValS that
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
    return "UnnamedVal(`" + type().name() + " = " + body + "`)";
  }
}
