package org.smoothbuild.compile.lang.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.type.SchemaS;

public sealed abstract class PolyEvaluableS extends NalImpl implements RefableS
    permits PolyFuncS, PolyValS {
  private final SchemaS schema;
  private final EvaluableS mono;

  public PolyEvaluableS(SchemaS schema, EvaluableS mono) {
    super(mono.name(), mono.loc());
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

  protected String polyEvaluableFieldsToString() {
    return joinToString("\n",
        "schema = " + schema,
        "mono = " + mono
    );
  }
}
