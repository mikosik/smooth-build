package org.smoothbuild.bytecode.type.val;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.collect.Sets.union;

import java.util.Set;
import java.util.stream.Collector;

import org.smoothbuild.lang.type.api.VarSet;

import com.google.common.collect.ImmutableList;

public final class VarSetB extends VarSet<VarTB> {
  public VarSetB(Set<VarTB> elements) {
    super(elements);
  }

  public static VarSetB varSetB(VarTB... vars) {
    return new VarSetB(Set.of(vars));
  }

  public static Collector<VarTB, Object, VarSetB> toVarSetB() {
    return collectingAndThen(toSet(), VarSetB::new);
  }

  @Override
  public ImmutableList<VarTB> asList() {
    return super.asList();
  }

  public VarSetB unionWith(VarSetB other) {
    return new VarSetB(union(elements, other.elements));
  }
}
