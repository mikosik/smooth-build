package org.smoothbuild.compile.lang.type;

/**
 * Polymorphic function type (= function type schema).
 */
public final class FuncSchemaS extends SchemaS {
  public FuncSchemaS(FuncTS funcTS) {
    super(funcTS);
  }

  @Override
  public FuncTS type() {
    return (FuncTS) super.type();
  }
}
