package org.smoothbuild.lang.type.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.collect.Sets.union;

import java.util.Set;
import java.util.stream.Collector;

import org.smoothbuild.lang.type.api.VarSet;

import com.google.common.collect.ImmutableList;

public final class VarSetS extends VarSet<VarS> {
  public VarSetS(Set<VarS> elements) {
    super(elements);
  }

  public static VarSetS varSetS(VarS... vars) {
    return new VarSetS(Set.of(vars));
  }

  public static Collector<VarS, Object, VarSetS> toVarSetS() {
    return collectingAndThen(toSet(), VarSetS::new);
  }

  @Override
  public ImmutableList<VarS> asList() {
    return super.asList();
  }

  public VarSetS unionWith(VarSetS other) {
    return new VarSetS(union(elements, other.elements));
  }
}
