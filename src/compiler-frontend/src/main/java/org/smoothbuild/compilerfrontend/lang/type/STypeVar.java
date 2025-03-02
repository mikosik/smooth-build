package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.compilerfrontend.lang.name.Name.typeName;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * Type variable.
 * This class is immutable.
 */
public final class STypeVar extends SType {
  private static final String FLEXIBLE_VAR_PREFIX = "T~";
  private final Set<STypeVar> typeVars;
  private final Name name;
  private final boolean isFlexible;

  public STypeVar(Name name) {
    this(name, false);
  }

  private STypeVar(Name name, boolean isFlexible) {
    super(null);
    this.name = name;
    this.typeVars = set(this);
    this.isFlexible = isFlexible;
  }

  public static STypeVar flexibleTypeVar(int i) {
    return new STypeVar(typeName(FLEXIBLE_VAR_PREFIX + i), true);
  }

  public static String typeParamsToSourceCode(List<STypeVar> typeParams) {
    return typeParams.map(STypeVar::name).toString("<", ",", ">");
  }

  public Name name() {
    return name;
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
  public String specifier() {
    return name.toString();
  }
}
