package org.smoothbuild.lang.type.api;

import static java.util.Comparator.comparing;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Set;
import java.util.stream.Stream;

import org.smoothbuild.util.collect.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public class VarSet<T extends Type> {
  protected final ImmutableSortedSet<T> elements;

  public VarSet(Set<T> elements) {
    this.elements = Sets.sort(elements, comparing(T::name));
  }

  public boolean contains(T type) {
    return elements.contains(type);
  }

  public boolean containsAll(VarSet<T> subset) {
    return elements.containsAll(subset.elements);
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public Stream<T> stream() {
    return elements.stream();
  }

  public ImmutableList<T> asList() {
    return ImmutableList.copyOf(elements);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof VarSet that
        && this.elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  @Override
  public String toString() {
    return "<" + toCommaSeparatedString(elements, Type::name) + ">";
  }
}
