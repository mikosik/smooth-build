package org.smoothbuild.lang.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.impl.VarSetS.toVarSetS;

import java.util.Objects;

import org.smoothbuild.lang.type.api.Type;

import com.google.common.collect.ImmutableList;

/**
 * Smooth language type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS implements Type
    permits ArrayTS, BaseTS, FuncTS, StructTS, VarS {
  private final VarSetS vars;
  private final String name;

  protected TypeS(String name, VarSetS vars) {
    checkArgument(!name.isBlank());
    this.name = name;
    this.vars = vars;
  }

  @Override
  public String name() {
    return name;
  }

  public static VarSetS calculateVars(ImmutableList<TypeS> types) {
    return types.stream()
        .map(TypeS::vars)
        .flatMap(VarSetS::stream)
        .collect(toVarSetS());
  }

  @Override
  public VarSetS vars() {
    return vars;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TypeS that
        && getClass().equals(object.getClass())
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}
