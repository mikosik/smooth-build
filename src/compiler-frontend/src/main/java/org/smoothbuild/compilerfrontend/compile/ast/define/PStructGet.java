package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public final class PStructGet extends PExpr {
  private final PExpr structExpr;
  private final String fieldNameText;
  private @Nullable Name fieldName;

  public PStructGet(PExpr structExpr, String fieldNameText, Location location) {
    super(location);
    this.structExpr = structExpr;
    this.fieldNameText = fieldNameText;
  }

  public PExpr structExpr() {
    return structExpr;
  }

  public String fieldNameText() {
    return fieldNameText;
  }

  public Name fieldName() {
    return checkInitializedToNotNull(fieldName, "fieldName");
  }

  public void setFieldName(Name fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PStructGet that
        && Objects.equals(this.structExpr, that.structExpr)
        && Objects.equals(this.fieldNameText, that.fieldNameText)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(structExpr, fieldNameText, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PStructGet")
        .addField("structExpr", structExpr)
        .addField("field", fieldNameText)
        .addField("location", location())
        .toString();
  }
}
