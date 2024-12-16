package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Name;

public final class PSelect extends PExpr {
  private final PExpr selectable;
  private final String fieldNameText;
  private Name fieldName;

  public PSelect(PExpr selectable, String fieldNameText, Location location) {
    super(location);
    this.selectable = selectable;
    this.fieldNameText = fieldNameText;
  }

  public PExpr selectable() {
    return selectable;
  }

  public String fieldNameText() {
    return fieldNameText;
  }

  public Name fieldName() {
    return fieldName;
  }

  public void setFieldName(Name fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PSelect that
        && Objects.equals(this.selectable, that.selectable)
        && Objects.equals(this.fieldNameText, that.fieldNameText)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectable, fieldNameText, location());
  }

  @Override
  public String toString() {
    var fields = list(
            "selectable = " + selectable, "field = " + fieldNameText, "location = " + location())
        .toString("\n");
    return "PSelect(\n" + indent(fields) + "\n)";
  }
}
