package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class NamedPolyValS extends PolyEvaluableImplS implements NamedPolyEvaluableS {
  public NamedPolyValS(SchemaS schema, ValS val) {
    super(schema, val);
  }

  @Override
  public String name() {
    return mono().name();
  }

  @Override
  public ValS mono() {
    return ((ValS) super.mono());
  }

  @Override
  public String toString() {
    return "NamedPolyValS(\n" + indent(fieldsToString()) + "\n)";
  }
}
