package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Comparator.comparing;

import com.google.common.collect.ImmutableSet;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.function.Function1;

public final class SVarSet extends Set<SVar> {
  public static SVarSet varSetS(SVar... vars) {
    var set = set(vars);
    return newSortedVarSetS(set);
  }

  public static SVarSet varSetS(Collection<? extends SType> types) {
    return newSortedVarSetS(setOfAll(types.toList().flatMap(SType::vars)));
  }

  private static SVarSet newSortedVarSetS(Set<SVar> set) {
    return new SVarSet(set.sort(comparing(SVar::fqn)));
  }

  private SVarSet(Set<SVar> elements) {
    super(ImmutableSet.copyOf(elements));
  }

  public <T extends Throwable> SVarSet mapVars(Function1<? super SVar, SVar, T> filter) throws T {
    return new SVarSet(super.map(filter));
  }

  @Override
  public <T extends Throwable> SVarSet filter(Function1<SVar, Boolean, T> predicate) throws T {
    return new SVarSet(super.filter(predicate));
  }

  public SVarSet addAll(Iterable<? extends SVar> toAdd) {
    return new SVarSet(unionWith(toAdd));
  }

  @Override
  public SVarSet removeAll(Collection<?> toRemove) {
    return new SVarSet(super.removeAll(toRemove));
  }

  public String toSourceCode() {
    return toString();
  }

  @Override
  public String toString() {
    return map(SVar::name).toString("<", ",", ">");
  }
}
