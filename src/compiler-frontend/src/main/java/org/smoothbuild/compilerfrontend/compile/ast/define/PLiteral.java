package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;

public abstract sealed class PLiteral extends PExpr permits PBlob, PInt, PString {
  private final String literal;

  public PLiteral(String literal, Location location) {
    super(location);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PLiteral that
        && this.getClass().equals(that.getClass())
        && Objects.equals(this.literal, that.literal)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(literal, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder(getClass().getName())
        .addField("literal", literal)
        .addField("location", location())
        .toString();
  }
}
