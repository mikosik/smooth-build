package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.q;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.SchemaS;

public final class RefP extends MonoizableP {
  private final String name;
  private SchemaS schemaS;

  public RefP(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
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
    return object instanceof RefP that
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.schemaS, that.schemaS);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, schemaS);
  }

  @Override
  public String toString() {
    return "RefP(`" + q(name) + "`)";
  }
}
