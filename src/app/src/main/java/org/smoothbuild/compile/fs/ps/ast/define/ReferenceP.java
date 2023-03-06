package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.SchemaS;

public final class ReferenceP extends PolymorphicP {
  private final String name;
  private SchemaS schemaS;

  public ReferenceP(String name, Location location) {
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
    return object instanceof ReferenceP that
        && Objects.equals(this.name, that.name)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "name = " + name,
        "location = " + location()
    );
    return "ReferenceP(\n" + indent(fields) + "\n)";
  }
}
