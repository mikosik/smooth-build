package org.smoothbuild.compile.lang.type;

import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;

import java.util.function.Function;

/**
 * Type variable.
 * This class is immutable.
 */
public sealed class VarS extends TypeS permits TempVarS {
  private final VarSetS vars;

  public VarS(String name) {
    super(name, null);
    this.vars = varSetS(this);
  }

  @Override
  public VarSetS vars() {
    return vars;
  }

  @Override
  public TypeS mapVars(Function<VarS, TypeS> varMapper) {
    return varMapper.apply(this);
  }

  public boolean isTemporary() {
    return this instanceof TempVarS;
  }
}
