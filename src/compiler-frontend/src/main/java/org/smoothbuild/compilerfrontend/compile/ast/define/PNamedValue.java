package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public final class PNamedValue extends PNamedEvaluable {
  private final PType type;

  public PNamedValue(
      PType type,
      String nameText,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
      Location location) {
    super(nameText, body, annotation, location);
    this.type = type;
  }

  public PType pType() {
    return type;
  }

  @Override
  public PType evaluationType() {
    return pType();
  }

  @Override
  public SType type() {
    return pType().sType();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PNamedValue that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.nameText(), that.nameText())
        && Objects.equals(this.body(), that.body())
        && Objects.equals(this.annotation(), that.annotation())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, nameText(), body(), annotation(), location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PNamedValue")
        .addField("type", type)
        .addField("name", nameText())
        .addField("body", body())
        .addField("annotation", annotation())
        .addField("location", location())
        .toString();
  }
}
