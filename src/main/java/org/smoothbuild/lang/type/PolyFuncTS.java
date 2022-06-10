package org.smoothbuild.lang.type;

/**
 * Polymorphic type.
 */
public final class PolyFuncTS extends PolyTS {
  public PolyFuncTS(VarSetS freeVars, MonoFuncTS monoFuncTS) {
    super(freeVars, monoFuncTS);
  }

  public static PolyFuncTS polyFuncTS(MonoFuncTS monoFuncTS) {
    return new PolyFuncTS(monoFuncTS.vars(), monoFuncTS);
  }

  @Override
  public MonoFuncTS type() {
    return (MonoFuncTS) super.type();
  }

  @Override
  public String q() {
    VarSetS vars = freeVars();
    String shortName = (vars.isEmpty() ? "" : vars.toString()) + type().name();
    return "`" + shortName + "`";
  }
}
