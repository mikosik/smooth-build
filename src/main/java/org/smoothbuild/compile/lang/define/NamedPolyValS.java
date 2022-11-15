package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import org.smoothbuild.compile.lang.type.SchemaS;

public final class NamedPolyValS extends PolyEvaluableImplS implements NamedPolyEvaluableS {
  public NamedPolyValS(SchemaS schema, NamedValueS namedValue) {
    super(schema, namedValue);
  }

  @Override
  public String name() {
    return mono().name();
  }

  @Override
  public NamedValueS mono() {
    return ((NamedValueS) super.mono());
  }

  @Override
  public String toString() {
    return "NamedPolyValS(\n" + indent(fieldsToString()) + "\n)";
  }
}
