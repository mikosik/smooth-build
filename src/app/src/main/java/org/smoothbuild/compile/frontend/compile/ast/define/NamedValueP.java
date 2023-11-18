package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

public final class NamedValueP extends NamedEvaluableP {
  private final TypeP type;
  private TypeS typeS;
  private SchemaS schemaS;

  public NamedValueP(
      TypeP type,
      String fullName,
      String shortName,
      Optional<ExprP> body,
      Optional<AnnotationP> annotation,
      Location location) {
    super(fullName, shortName, body, annotation, location);
    this.type = type;
  }

  public TypeP type() {
    return type;
  }

  @Override
  public TypeP evaluationT() {
    return type();
  }

  @Override
  public TypeS typeS() {
    return typeS;
  }

  public void setTypeS(TypeS typeS) {
    this.typeS = typeS;
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
    return object instanceof NamedValueP that
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
    var fields = joinToString("\n",
        "type = " + type,
        "name = " + name(),
        "body = " + body(),
        "annotation = " + annotation(),
        "location = " + location()
    );
    return "NamedValueP(\n" + indent(fields) + "\n)";
  }
}
