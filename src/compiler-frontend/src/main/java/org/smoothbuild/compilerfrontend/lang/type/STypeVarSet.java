package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Comparator.comparing;

import com.google.common.collect.ImmutableSet;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.function.Function1;

public final class STypeVarSet extends Set<STypeVar> {
  public static STypeVarSet sTypeVarSet(STypeVar... typeVars) {
    var set = set(typeVars);
    return newSortedTypeVarSet(set);
  }

  public static STypeVarSet sTypeVarSet(Collection<? extends SType> types) {
    return newSortedTypeVarSet(setOfAll(types.toList().flatMap(SType::typeVars)));
  }

  private static STypeVarSet newSortedTypeVarSet(Set<STypeVar> set) {
    return new STypeVarSet(set.sort(comparing(STypeVar::fqn)));
  }

  private STypeVarSet(Set<STypeVar> elements) {
    super(ImmutableSet.copyOf(elements));
  }

  public <T extends Throwable> STypeVarSet mapVars(Function1<? super STypeVar, STypeVar, T> filter)
      throws T {
    return new STypeVarSet(super.map(filter));
  }

  @Override
  public <T extends Throwable> STypeVarSet filter(Function1<STypeVar, Boolean, T> predicate)
      throws T {
    return new STypeVarSet(super.filter(predicate));
  }

  public STypeVarSet addAll(Iterable<? extends STypeVar> toAdd) {
    return new STypeVarSet(unionWith(toAdd));
  }

  @Override
  public STypeVarSet removeAll(Collection<?> toRemove) {
    return new STypeVarSet(super.removeAll(toRemove));
  }

  public String toSourceCode() {
    return toShortString();
  }

  public String q() {
    return Strings.q(toShortString());
  }

  public String toShortString() {
    return map(STypeVar::name).toString("<", ",", ">");
  }

  @Override
  public String toString() {
    return map(STypeVar::fqn).toString("<", ",", ">");
  }
}
