package org.smoothbuild.compile.lang.type;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.Sets.union;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.smoothbuild.util.collect.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public final class VarSetS implements Set<VarS> {
  private final ImmutableSortedSet<VarS> elements;

  public static VarSetS varSetS(VarS... vars) {
    return new VarSetS(Set.of(vars));
  }

  public static VarSetS varSetS(Collection<TypeS> types) {
    return types.stream()
        .flatMap(t -> t.vars().stream())
        .collect(toVarSetS());
  }

  public VarSetS(Set<VarS> elements) {
    this.elements = Sets.sort(elements, comparing(VarS::name));
  }

  public static Collector<VarS, Object, VarSetS> toVarSetS() {
    return collectingAndThen(toSet(), VarSetS::new);
  }

  public VarSetS filter(Predicate<? super VarS> predicate) {
    return stream()
        .filter(predicate)
        .collect(toVarSetS());
  }

  public ImmutableList<VarS> asList() {
    return elements.asList();
  }

  public VarSetS unionWith(VarSetS other) {
    return new VarSetS(union(elements, other.elements));
  }

  // overrides from Set<VarS>

  @Override
  public boolean contains(Object object) {
    return elements.contains(object);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return elements.containsAll(collection);
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  @Override
  public Iterator<VarS> iterator() {
    return elements.iterator();
  }

  @Override
  public Object[] toArray() {
    return elements.toArray();
  }

  @Override
  public <T> T[] toArray(T[] array) {
    return elements.toArray(array);
  }

  @Override
  public boolean add(VarS var) {
    return elements.add(var);
  }

  @Override
  public boolean remove(Object object) {
    return elements.remove(object);
  }

  @Override
  public boolean addAll(Collection<? extends VarS> collection) {
    return elements.addAll(collection);
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return elements.retainAll(collection);
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return elements.removeAll(collection);
  }

  @Override
  public void clear() {
    elements.clear();
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public Stream<VarS> stream() {
    return elements.stream();
  }

  @Override
  public void forEach(Consumer<? super VarS> consumer) {
    elements.forEach(consumer);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof VarSetS that
        && this.elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  @Override
  public String toString() {
    return "<" + toCommaSeparatedString(elements, VarS::name) + ">";
  }
}

