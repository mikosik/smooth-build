package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public final class PNamedValue extends PNamedEvaluable {
  private final PType type;
  private SType sType;
  private SchemaS schemaS;

  public PNamedValue(
      PType type,
      String fullName,
      String shortName,
      Maybe<PExpr> body,
      Maybe<PAnnotation> annotation,
      Location location) {
    super(fullName, shortName, body, annotation, location);
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
  public SType typeS() {
    return sType;
  }

  public void setTypeS(SType sType) {
    this.sType = sType;
  }

  @Override
  public SchemaS schemaS() {
    return schemaS;
  }

  public void setSchemaS(SchemaS schemaS) {
    this.schemaS = schemaS;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PNamedValue that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.body(), that.body())
        && Objects.equals(this.annotation(), that.annotation())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name(), body(), annotation(), location());
  }

  @Override
  public String toString() {
    var fields = list(
            "type = " + type,
            "name = " + name(),
            "body = " + body(),
            "annotation = " + annotation(),
            "location = " + location())
        .toString("\n");
    return "NamedValueP(\n" + indent(fields) + "\n)";
  }
}
