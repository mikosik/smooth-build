package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.SchemaS;

/**
 * Defined value (one that has a body).
 * This class is immutable.
 */
public final class DefValueS extends NamedValueS {
  private final ExprS body;

  public DefValueS(SchemaS schema, String name, ExprS body, Loc loc) {
    super(schema, name, loc);
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
    return object instanceof DefValueS that
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.body().equals(that.body())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), name(), body(), loc());
  }

  @Override
  public String toString() {
    var fieldsString = fieldsToString() + "\nbody = " + body;
    return "DefVal(\n" + indent(fieldsString) + "\n)";
  }
}


