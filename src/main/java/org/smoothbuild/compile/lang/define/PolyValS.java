package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class PolyValS extends PolyEvaluableS {
  public PolyValS(SchemaS schema, ValS val) {
    super(schema, val);
  }

  @Override
  public ValS mono() {
    return ((ValS) super.mono());
  }

  @Override
  public String toString() {
    return "PolyValS(\n" + indent(polyEvaluableFieldsToString()) + "\n)";
  }
}
