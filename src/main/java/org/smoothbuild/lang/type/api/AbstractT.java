package org.smoothbuild.lang.type.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.lang.type.impl.OpenVarTS;
import org.smoothbuild.lang.type.impl.TypeS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public non-sealed abstract class AbstractT implements Type {
  protected final String name;
  private final ImmutableSet<OpenVarTS> openVars;
  private final boolean hasClosedVars;

  public AbstractT(String name, ImmutableSet<OpenVarTS> openVars, boolean hasClosedVars) {
    checkArgument(!name.isBlank());
    this.openVars = openVars;
    this.hasClosedVars = hasClosedVars;
    this.name = name;
  }

  public static ImmutableSet<OpenVarTS> calculateOpenVars(ImmutableList<TypeS> types) {
    return types.stream()
        .map(TypeS::openVars)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean hasClosedVars() {
    return hasClosedVars;
  }

  @Override
  public ImmutableSet<OpenVarTS> openVars() {
    return openVars;
  }
}
