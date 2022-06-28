package org.smoothbuild.lang.type.solver;

import static com.google.common.collect.Collections2.permutations;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.testing.type.TestingTS;
import org.smoothbuild.util.collect.Sets;

@TestInstance(PER_CLASS)
public class SolverSTest extends TestingTS {
  @Test
  public void without_constraints() throws Exception {
    assertSolvedGraph(list(), ConstrGraph.builder().build());
  }

  @ParameterizedTest
  @MethodSource
  public void var_lower_bound_is_propagated_to_upper_var(List<ConstrS> constrs) throws Exception {
    assertSolvedGraph(constrs, ConstrGraph.builder()
        .addVar(varA(), list(varB()), bounds(bool(), any()))
        .addVar(varB(), list(), bounds(bool(), any()))
        .build());
  }

  public List<Arguments> var_lower_bound_is_propagated_to_upper_var() {
    return permutate(set(
        constrS(bool(), varA()),
        constrS(varA(), varB())
    ));
  }

  @ParameterizedTest
  @MethodSource
  public void var_lower_bound_is_propagated_to_upper_vars_transitively(List<ConstrS> constrs)
      throws Exception {
    assertSolvedGraph(constrs, ConstrGraph.builder()
        .addVar(varA(), list(varB(), varC()), bounds(bool(), any()))
        .addVar(varB(), list(varC()), bounds(bool(), any()))
        .addVar(varC(), list(), bounds(bool(), any()))
        .build());
  }

  public List<Arguments> var_lower_bound_is_propagated_to_upper_vars_transitively() {
    return permutate(set(
        constrS(bool(), varA()),
        constrS(varA(), varB()),
        constrS(varB(), varC())
    ));
  }

  @ParameterizedTest
  @MethodSource
  public void var_upper_bound_is_propagated_to_lower_var(List<ConstrS> constrs) throws Exception {
    assertSolvedGraph(constrs, ConstrGraph.builder()
        .addVar(varA(), list(varB()), bounds(nothing(), bool()))
        .addVar(varB(), list(), bounds(nothing(), bool()))
        .build());
  }

  public List<Arguments> var_upper_bound_is_propagated_to_lower_var() {
    return permutate(set(
        constrS(varA(), varB()),
        constrS(varB(), bool())
    ));
  }

  @ParameterizedTest
  @MethodSource
  public void var_upper_bound_is_propagated_to_lower_vars_transitively(List<ConstrS> constrs)
      throws Exception {
    assertSolvedGraph(constrs, ConstrGraph.builder()
        .addVar(varA(), list(varB(), varC()), bounds(nothing(), bool()))
        .addVar(varB(), list(varC()), bounds(nothing(), bool()))
        .addVar(varC(), list(), bounds(nothing(), bool()))
        .build());
  }

  public List<Arguments> var_upper_bound_is_propagated_to_lower_vars_transitively() {
    return permutate(set(
        constrS(varA(), varB()),
        constrS(varB(), varC()),
        constrS(varC(), bool())
    ));
  }

  @Test
  public void var_vs_var() throws Exception {
    assertSolvedGraph(list(constrS(varA(), varB())), ConstrGraph.builder()
            .addVar(varA(), list(varB()), bounds())
            .addVar(varB(), list(), bounds())
            .build()
        );
  }

  @Nested
  @TestInstance(PER_CLASS)
  class _array {
    @ParameterizedTest
    @MethodSource
    public void propagation_of_element_upper_bound(List<ConstrS> constrs) throws Exception {
      assertSolvedGraph(constrs, ConstrGraph.builder()
          .addVar(varA(), list(), bounds(nothing(), bool()))
          .build());
    }

    public List<Arguments> propagation_of_element_upper_bound() {
      return permutate(set(
          constrS(array(varA()), array(bool()))
      ));
    }

    @ParameterizedTest
    @MethodSource
    public void propagation_of_element_upper_bound_through_var(List<ConstrS> constrs)
        throws Exception {
      assertSolvedGraph(constrs, ConstrGraph.builder()
          .addVar(varA(), list(v0(), v1()), bounds(nothing(), int_()))
          .addVar(varB(), list(), bounds(array(v0()), array(v1())))
          .addVar(v0(), list(v1()), bounds(nothing(), int_()))
          .addVar(v1(), list(), bounds(nothing(), int_()))
          .build());
    }

    public List<Arguments> propagation_of_element_upper_bound_through_var() {
      return permutate(set(
          constrS(array(varA()), varB()),
          constrS(varB(), array(int_()))
      ));
    }
  }

  @Nested
  @TestInstance(PER_CLASS)
  class _func {
    @Test
    public void id() throws Exception {
      var constr = constrS(func(varA(), list(varA())), func(int_(), list(int_())));
      assertSolvedGraph(list(constr), ConstrGraph.builder()
          .addVar(varA(), list(), bounds(int_(), int_()))
          .build());
    }
  }

  @ParameterizedTest
  @MethodSource
  public void legal_var_loop(List<ConstrS> constrs) throws Exception {
    assertSolvedGraph(constrs, ConstrGraph.builder()
        .addVar(varA(), list(varB(), varC()), bounds())
        .addVar(varB(), list(varC(), varA()), bounds())
        .addVar(varC(), list(varA(), varB()), bounds())
        .build());
  }

  public List<Arguments> legal_var_loop() {
    return permutate(set(
        constrS(varA(), varB()),
        constrS(varB(), varC()),
        constrS(varC(), varA())));
  }

  private void assertSolvedGraph(List<ConstrS> constrs, ConstrGraph expected) throws Exception {
    var solver = new SolverS();
    for (var constr : constrs) {
      solver.addConstr(constr);
    }
    var actual = solver.graph();
    // `actual` can differ regarding order in which temporary variables were generated so
    // we remap `actual` for each permutation of temporary variables and check whether it is
    // equal to expected.
    var graph = findGraphWithRemappedTempVars(actual, expected);
    assertThat(graph)
        .isEqualTo(expected);
  }

  /**
   * Remap temporary vars in `actual` so they match `expected` and return it.
   * Otherwise, return `actual` unchanged.
   */
  private ConstrGraph findGraphWithRemappedTempVars(ConstrGraph actual, ConstrGraph expected) {
    var tempVars = actual.varBounds().keySet()
        .stream()
        .filter(v -> v.name().startsWith("_"))
        .sorted(comparing(MonoTS::name))
        .collect(toImmutableList());
    for (var permutation : permutations(tempVars)) {
      var remapped = mapVars(actual, varMapper(tempVars, permutation));
      if (remapped.equals(expected)) {
        return remapped;
      }
    }
    return actual;
  }

  private ConstrGraph mapVars(ConstrGraph graph, Function<VarS, VarS> mapper) {
    var builder = ConstrGraph.builder();
    for (var entry : graph.varBounds().entrySet()) {
      var newVar = mapVars(entry.getKey(), mapper);
      var oldBounds = entry.getValue();
      var newBounds = bounds(
          mapVars(oldBounds.lower(), mapper),
          mapVars(oldBounds.upper(), mapper));
      builder.addBounds(newVar, newBounds);
    }
    for (var entry : graph.constrs().entries()) {
      builder.addUpper(mapVars(entry.getKey(), mapper), mapVars(entry.getValue(), mapper));
    }
    return builder.build();
  }

  private Function<VarS, VarS> varMapper(List<VarS> keys, List<VarS> values) {
    Map<VarS, VarS> map = new HashMap<>();
    for (int i = 0; i < keys.size(); i++) {
      map.put(keys.get(i), values.get(i));
    }
    return var -> map.computeIfAbsent(var, v -> v);
  }

  private MonoTS mapVars(MonoTS type, Function<VarS, VarS> mapper) {
    return switch (type) {
      case AnyTS any -> any;
      case ArrayTS array -> array(mapVars(array.elem(), mapper));
      case BlobTS blob -> blob;
      case BoolTS bool -> bool;
      case MonoFuncTS func -> func(
          mapVars(func.res(), mapper), map(func.params(), p -> mapVars(p, mapper)));
      case IntTS int_ -> int_;
      case JoinTS join -> join(Sets.map(join.elems(), e -> mapVars(e, mapper)));
      case MeetTS meet -> meet(Sets.map(meet.elems(), e -> mapVars(e, mapper)));
      case NothingTS nothing -> nothing;
      case StringTS string -> string;
      case StructTS struct -> struct;
      case VarS var -> mapVars(var, mapper);
    };
  }

  private VarS mapVars(VarS var, Function<VarS, VarS> mapper) {
    return mapper.apply(var);
  }

  private static List<Arguments> permutate(Set<ConstrS> constrs) {
    // Each permutation of constraints is tested to make sure that order is not relevant
    // when passing constraints to Solver.
    return permutations(constrs)
        .stream()
        .map(Arguments::of)
        .toList();
  }
}
