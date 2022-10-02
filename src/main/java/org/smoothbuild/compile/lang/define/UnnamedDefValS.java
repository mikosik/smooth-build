package org.smoothbuild.compile.lang.define;

import java.util.Objects;

/**
 * Unnamed Defined value (one that has a body).
 * This class is immutable.
 */
public final class UnnamedDefValS extends EvaluableS {
  private final ExprS body;

  public UnnamedDefValS(ExprS body) {
    super(body.type(), body.loc());
    this.body = body;
  }

  public ExprS body() {
    return body;
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
