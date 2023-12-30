package org.smoothbuild.compile.frontend.lang.type;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.collect.Set.setOfAll;

import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Iterator;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.function.Function1;

public final class VarSetS extends java.util.AbstractSet<VarS> {
  private final Set<VarS> elements;

  public static VarSetS varSetS(VarS... vars) {
    var set = set(vars);
    return newSortedVarSetS(set);
  }

  public static VarSetS varSetS(Iterable<? extends TypeS> types) {
    return newSortedVarSetS(
        setOfAll(Streams.stream(types).flatMap(t -> t.vars().stream()).collect(toSet())));
  }

  private static VarSetS newSortedVarSetS(Set<VarS> set) {
    return new VarSetS(set.sort(comparing(VarS::name)));
  }

  private VarSetS(Set<VarS> elements) {
    this.elements = elements;
  }

  public <R, T extends Throwable> Set<R> map(Function1<? super VarS, R, T> filter) throws T {
    return elements.map(filter);
  }

  public <T extends Throwable> VarSetS filter(Function1<VarS, Boolean, T> predicate) throws T {
    return new VarSetS(elements.filter(predicate));
  }

  public List<VarS> toList() {
    return elements.toList();
  }

  public VarSetS withAddedAll(Iterable<? extends VarS> toAdd) {
    return new VarSetS(elements.unionWith(toAdd));
  }

  public VarSetS withRemovedAll(Collection<?> toRemove) {
    return new VarSetS(elements.withRemovedAll(toRemove));
  }

  @Override
  public Iterator<VarS> iterator() {
    return elements.iterator();
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public String toString() {
    return listOfAll(elements).map(VarS::name).toString("<", ",", ">");
  }
}
