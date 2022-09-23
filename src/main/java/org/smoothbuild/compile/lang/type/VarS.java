package org.smoothbuild.compile.lang.type;

import static org.smoothbuild.util.collect.Sets.set;

import java.util.function.Function;

/**
 * Type variable.
 * This class is immutable.
 */
public sealed class VarS extends TypeS permits TempVarS {
  private final VarSetS vars;

  public VarS(String name) {
    super(name, null);
    this.vars = new VarSetS(set(this));
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
