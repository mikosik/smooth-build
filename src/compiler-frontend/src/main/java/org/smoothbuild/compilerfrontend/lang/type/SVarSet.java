package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Comparator.comparing;

import com.google.common.collect.ImmutableSet;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.function.Function1;

public final class SVarSet extends Set<STypeVar> {
  public static SVarSet sVarSet(STypeVar... typeVars) {
    var set = set(typeVars);
    return newSortedSVarSet(set);
  }

  public static SVarSet sVarSet(Collection<? extends SType> types) {
    return newSortedSVarSet(setOfAll(types.toList().flatMap(SType::vars)));
  }

  private static SVarSet newSortedSVarSet(Set<STypeVar> set) {
    return new SVarSet(set.sort(comparing(STypeVar::fqn)));
  }

  private SVarSet(Set<STypeVar> elements) {
    super(ImmutableSet.copyOf(elements));
  }

  public <T extends Throwable> SVarSet mapVars(Function1<? super STypeVar, STypeVar, T> filter)
      throws T {
    return new SVarSet(super.map(filter));
  }

  @Override
  public <T extends Throwable> SVarSet filter(Function1<STypeVar, Boolean, T> predicate) throws T {
    return new SVarSet(super.filter(predicate));
  }

  public SVarSet addAll(Iterable<? extends STypeVar> toAdd) {
    return new SVarSet(unionWith(toAdd));
  }

  @Override
  public SVarSet removeAll(Collection<?> toRemove) {
    return new SVarSet(super.removeAll(toRemove));
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
