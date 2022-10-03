package org.smoothbuild.compile.lang.define;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.WithLocImpl;
import org.smoothbuild.compile.lang.type.SchemaS;

public sealed abstract class PolyEvaluableS extends WithLocImpl
    permits NamedPolyEvaluableS, UnnamedPolyValS {
  private final SchemaS schema;
  private final EvaluableS mono;

  public PolyEvaluableS(SchemaS schema, EvaluableS mono) {
    super(mono.loc());
    this.schema = requireNonNull(schema);
    this.mono = requireNonNull(mono);
  }

  public SchemaS schema() {
    return schema;
  }

  public EvaluableS mono() {
    return mono;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PolyEvaluableS poly
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
