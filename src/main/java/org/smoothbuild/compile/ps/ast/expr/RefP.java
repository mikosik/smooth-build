package org.smoothbuild.compile.ps.ast.expr;

import static java.util.Objects.requireNonNullElse;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.TypelikeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public final class RefP extends NalImpl implements ExprP {
  private ImmutableMap<VarS, ? extends TypeS> monoizationMapping;
  private TypelikeS typelike;

  public RefP(String name, Loc loc) {
    super(name, loc);
  }

  @Override
  public String toString() {
    return "RefP(`" + q() + "`)";
  }

  public void setMonoizationMapping(ImmutableMap<VarS, ? extends TypeS> monoizationMapping) {
    this.monoizationMapping = monoizationMapping;
  }

  public ImmutableMap<VarS, ? extends TypeS> monoizationMapping() {
    return monoizationMapping;
  }

  public Function<VarS, TypeS> monoizationMapper() {
    return key -> requireNonNullElse(monoizationMapping.get(key), key);
  }

  public void setTypelike(TypelikeS typelike) {
    this.typelike = typelike;
  }

  public TypelikeS typelike() {
    return typelike;
  }
}
