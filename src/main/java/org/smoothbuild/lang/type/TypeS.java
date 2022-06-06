package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.type.VarSetS.toVarSetS;
import static org.smoothbuild.lang.type.VarSetS.varSetS;

import java.util.Objects;

import com.google.common.collect.ImmutableCollection;

/**
 * Smooth language type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS
    permits ArrayTS, BaseTS, FuncTS, MergeTS, StructTS, VarS {
  private final VarSetS vars;
  private final String name;

  protected TypeS(String name) {
    this(name, varSetS());
  }

  protected TypeS(String name, VarSetS vars) {
    checkArgument(!name.isBlank());
    this.name = name;
    this.vars = vars;
  }

  public String name() {
    return name;
  }

  public String q() {
    return "`" + name() + "`";
  }

  public static VarSetS calculateVars(ImmutableCollection<TypeS> types) {
    return types.stream()
        .map(TypeS::vars)
        .flatMap(VarSetS::stream)
        .collect(toVarSetS());
  }

  public VarSetS vars() {
    return vars;
  }

  public TypeS withPrefixedVars(String prefix) {
    return this;
  }

//  public abstract TypeS removeVarPrefixes(String prefix);
  public TypeS removeVarPrefixes() {
    return this;
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
    return name();
  }
}
