package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.Set.set;

import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Type variable.
 * This class is immutable.
 */
public final class STypeVar extends SType implements Identifiable {
  private static final String FLEXIBLE_VAR_PREFIX = "T~";
  private final Set<STypeVar> typeVars;
  private final Fqn fqn;
  private final boolean isFlexible;

  public STypeVar(Fqn fqn) {
    this(fqn, false);
  }

  private STypeVar(Fqn fqn, boolean isFlexible) {
    super(null);
    this.fqn = fqn;
    this.typeVars = set(this);
    this.isFlexible = isFlexible;
  }

  public static STypeVar flexibleTypeVar(int i) {
    return new STypeVar(Fqn.fqn(FLEXIBLE_VAR_PREFIX + i), true);
  }

  public static String typeParamsToSourceCode(List<STypeVar> typeParams) {
    return typeParams.map(STypeVar::name).toString("<", ",", ">");
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public Set<STypeVar> typeVars() {
    return typeVars;
  }

  @Override
  public boolean isFlexibleTypeVar() {
    return isFlexible;
  }

  @Override
  public String specifier(Collection<STypeVar> localTypeVars) {
    return localTypeVars.contains(this) ? name().toString() : fqn.toString();
  }
}
