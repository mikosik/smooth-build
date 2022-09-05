package org.smoothbuild.compile.lang.define;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Panal;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypelikeS;

public sealed abstract class PolyRefableS extends Panal implements RefableS
    permits PolyFuncS, PolyValS {
  private final SchemaS schema;
  private final MonoRefableS mono;

  public PolyRefableS(SchemaS schema, MonoRefableS mono) {
    super(mono.modPath(), mono.name(), mono.loc());
    this.schema = requireNonNull(schema);
    this.mono = requireNonNull(mono);
  }

  @Override
  public TypelikeS typelike() {
    return schema;
  }

  public SchemaS schema() {
    return schema;
  }

  public MonoRefableS mono() {
    return mono;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PolyRefableS poly
        && schema.equals(poly.schema)
        && mono.equals(poly.mono);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, mono);
  }

  @Override
  public String toString() {
    return schema.quantifiedVars() + mono.toString();
  }
}
