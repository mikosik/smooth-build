package org.smoothbuild.lang.type;

/**
 * Polymorphic type.
 */
public final class PolyFuncTS extends PolyTS {
  public PolyFuncTS(VarSetS freeVars, FuncTS funcTS) {
    super(freeVars, funcTS);
  }

  public static PolyFuncTS polyFuncTS(FuncTS funcTS) {
    return new PolyFuncTS(funcTS.vars(), funcTS);
  }

  @Override
  public FuncTS type() {
    return (FuncTS) super.type();
  }

  @Override
  public String q() {
    VarSetS vars = freeVars();
    String shortName = (vars.isEmpty() ? "" : vars.toString()) + type().name();
    return "`" + shortName + "`";
  }
}
