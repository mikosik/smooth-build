package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Defined value (one that has a body).
 * This class is immutable.
 */
public final class DefValS extends ValS {
  private final ExprS body;

  public DefValS(TypeS type, String name, ExprS body, Loc loc) {
    super(type, name, loc);
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
    var fieldsString = valFieldsToString() + "\nbody = " + body;
    return "DefVal(\n" + indent(fieldsString) + "\n)";
  }
}


