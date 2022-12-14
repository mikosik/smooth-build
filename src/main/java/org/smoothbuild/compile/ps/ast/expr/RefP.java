package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.q;

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
  public String toString() {
    return "RefP(`" + q(name) + "`)";
  }
}
