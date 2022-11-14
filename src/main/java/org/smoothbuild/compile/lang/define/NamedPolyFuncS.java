package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;

import org.smoothbuild.compile.lang.type.FuncSchemaS;

public final class NamedPolyFuncS extends PolyFuncS implements NamedPolyEvaluableS {
  public NamedPolyFuncS(FuncSchemaS schema, NamedFuncS funcS) {
    super(schema, funcS);
  }

  @Override
  public String name() {
    return mono().name();
  }

  @Override
  public NamedFuncS mono() {
    return (NamedFuncS) super.mono();
  }

  @Override
  public String toString() {
    return "NamedPolyFuncS(\n" + indent(fieldsToString()) + "\n)";
  }
}
