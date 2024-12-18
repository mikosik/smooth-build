package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Comparator.comparing;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.collect.Set.setOfAll;

import java.util.Iterator;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.function.Function1;

public final class SVarSet extends java.util.AbstractSet<SVar> {
  private final Set<SVar> elements;

  public static SVarSet varSetS(SVar... vars) {
    var set = set(vars);
    return newSortedVarSetS(set);
  }

  public static SVarSet varSetS(Collection<? extends SType> types) {
    return newSortedVarSetS(setOfAll(types.toList().flatMap(SType::vars)));
  }

  private static SVarSet newSortedVarSetS(Set<SVar> set) {
    return new SVarSet(set.sort(comparing(SVar::name)));
  }

  private SVarSet(Set<SVar> elements) {
    this.elements = elements;
  }

  public <R, T extends Throwable> Set<R> map(Function1<? super SVar, R, T> filter) throws T {
    return elements.map(filter);
  }

  public <T extends Throwable> SVarSet filter(Function1<SVar, Boolean, T> predicate) throws T {
    return new SVarSet(elements.filter(predicate));
  }

  public List<SVar> toList() {
    return elements.toList();
  }

  public SVarSet withAddedAll(Iterable<? extends SVar> toAdd) {
    return new SVarSet(elements.unionWith(toAdd));
  }

  public SVarSet withRemovedAll(Collection<?> toRemove) {
    return new SVarSet(elements.withRemovedAll(toRemove));
  }

  @Override
  public Iterator<SVar> iterator() {
    return elements.iterator();
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public String toString() {
    return listOfAll(elements).map(SVar::name).toString("<", ",", ">");
  }
}
