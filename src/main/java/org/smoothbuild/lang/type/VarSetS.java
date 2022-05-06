package org.smoothbuild.lang.type;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.Sets.union;

import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.smoothbuild.util.collect.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public final class VarSetS {
  private final ImmutableSortedSet<VarS> elements;

  public static VarSetS varSetS(VarS... vars) {
    return new VarSetS(Set.of(vars));
  }

  public VarSetS(Set<VarS> elements) {
    this.elements = Sets.sort(elements, comparing(VarS::name));
  }

  public static Collector<VarS, Object, VarSetS> toVarSetS() {
    return collectingAndThen(toSet(), VarSetS::new);
  }

  public boolean contains(VarS var) {
    return elements.contains(var);
  }

  public boolean containsAll(VarSetS subset) {
    return elements.containsAll((subset).elements);
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public Stream<VarS> stream() {
    return elements.stream();
  }

  public ImmutableList<VarS> asList() {
    return elements.asList();
  }

  public VarSetS unionWith(VarSetS other) {
    return new VarSetS(union(elements, other.elements));
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

