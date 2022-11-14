package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.WithLocImpl;
import org.smoothbuild.compile.lang.type.SchemaS;

public abstract class PolyEvaluableImplS extends WithLocImpl {
  private final SchemaS schema;
  private final EvaluableS mono;

  public PolyEvaluableImplS(SchemaS schema, EvaluableS mono) {
    super(mono.loc());
    this.schema = schema;
    this.mono = mono;
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
    return object instanceof PolyEvaluableImplS poly
        && getClass().equals(poly.getClass())
        && schema().equals(poly.schema())
        && mono().equals(poly.mono());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), mono());
  }

  protected String fieldsToString() {
    return joinToString("\n",
        "schema = " + schema(),
        "mono = " + mono()
    );
  }
}
