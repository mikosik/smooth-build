package org.smoothbuild.bytecode.type.val;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.Sets.union;

import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.smoothbuild.lang.type.api.VarSet;
import org.smoothbuild.util.collect.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

public final class VarSetB implements VarSet<VarB> {
  private final ImmutableSortedSet<VarB> elements;

  public VarSetB(Set<VarB> elements) {
    this.elements = Sets.sort(elements, comparing(VarB::name));
  }

  public static VarSetB varSetB(VarB... vars) {
    return new VarSetB(Set.of(vars));
  }

  public static Collector<VarB, Object, VarSetB> toVarSetB() {
    return collectingAndThen(toSet(), VarSetB::new);
  }

  @Override
  public boolean contains(VarB type) {
    return elements.contains(type);
  }

  @Override
  public boolean containsAll(VarSet<VarB> subset) {
    return elements.containsAll(((VarSetB) subset).elements);
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  @Override
  public Stream<VarB> stream() {
    return elements.stream();
  }

  @Override
  public ImmutableList<VarB> asList() {
    return elements.asList();
  }

  public VarSetB unionWith(VarSetB other) {
    return new VarSetB(union(elements, other.elements));
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof VarSetB that
        && this.elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  @Override
  public String toString() {
    return "<" + toCommaSeparatedString(elements, VarB::name) + ">";
  }
}
