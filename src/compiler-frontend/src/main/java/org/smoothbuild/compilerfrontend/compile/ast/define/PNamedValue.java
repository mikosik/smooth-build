package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public final class PNamedValue extends PNamedEvaluable {
  private final PType type;
  private SType sType;
  private SSchema sSchema;

  public PNamedValue(
      PType type,
      String nameText,
      PTypeParams typeParams,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
      Location location) {
    super(nameText, typeParams, body, annotation, location);
    this.type = type;
  }

  public PType type() {
    return type;
  }

  @Override
  public PType evaluationType() {
    return type();
  }

  @Override
  public SType sType() {
    return sType;
  }

  public void setSType(SType sType) {
    this.sType = sType;
  }

  @Override
  public SSchema schema() {
    return sSchema;
  }

  public void setSchema(SSchema schema) {
    this.sSchema = schema;
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
