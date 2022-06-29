package org.smoothbuild.lang.type.solver;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.lang.type.EdgeTS.edgeTS;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.Side.UPPER;
import static org.smoothbuild.lang.type.solver.Decompose.decompose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BaseTS;
import org.smoothbuild.lang.type.Bounds;
import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.MergeTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.Side;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public class DeduceVarMap {
  private static final ResolveMerges TYPING = new ResolveMerges();

  /**
   * For given assignment `source -> target` deduce what types should be assigned to vars
   * in `source` so `source` is assignable to `target`.
   */
  public static ImmutableMap<VarS, MonoTS> deduceVarMap(MonoTS source, MonoTS target) {
    Map<VarS, Bounds<Set<MonoTS>>> deducedBounds = new HashMap<>();
    deduceVarMap(source, target, UPPER, deducedBounds);
    return deducedBounds.entrySet().stream()
        .collect(toImmutableMap(Entry::getKey, DeduceVarMap::reducedBounds));
  }

  private static void deduceVarMap(MonoTS source, MonoTS target, Side side, Map<VarS,
      Bounds<Set<MonoTS>>> deducedBounds) {
    switch (source) {
      case VarS var -> handleVar(var, target, side, deducedBounds);
      case ArrayTS arrayKey -> handleArray(arrayKey, target, side, deducedBounds);
      case MonoFuncTS funcKey -> handleFunc(funcKey, target, side, deducedBounds);
      case BaseTS base -> checkAssignment(base, target, side);
      case StructTS struct -> checkAssignment(struct, target, side);
      case MergeTS merge -> throw new IllegalArgumentException();
    }
  }

  private static void handleVar(VarS var, MonoTS target, Side side,
      Map<VarS, Bounds<Set<MonoTS>>> deducedBounds) {
    deducedBounds.computeIfAbsent(var, varS -> new Bounds<>(new HashSet<>(), new HashSet<>()))
        .get(side).add(target);
  }

  private static void handleArray(ArrayTS arrayKey, MonoTS target, Side side, Map<VarS,
      Bounds<Set<MonoTS>>> deducedBounds) {
    ArrayTS arrayValue = castToArray(target);
    deduceVarMap(arrayKey.elem(), arrayValue.elem(), side, deducedBounds);
  }

  private static ArrayTS castToArray(MonoTS array) {
    return switch (array) {
      case ArrayTS arrayTS -> arrayTS;
      default -> throw new IllegalArgumentException("Expected " + ArrayTS.class.getCanonicalName()
          + " got " + array.getClass().getCanonicalName() + ".");
    };
  }

  private static void handleFunc(MonoFuncTS funcKey, MonoTS target, Side side, Map<VarS,
      Bounds<Set<MonoTS>>> deducedBounds) {
    MonoFuncTS funcValue = castToFunc(target);
    deduceVarMap(funcKey.res(), funcValue.res(), side, deducedBounds);
    var paramsKey = funcKey.params();
    var paramsValue = funcValue.params();
    for (int i = 0; i < paramsKey.size(); i++) {
      deduceVarMap(paramsKey.get(i), paramsValue.get(i), side.other(), deducedBounds);
    }
  }

  private static MonoFuncTS castToFunc(MonoTS func) {
    return switch (func) {
      case MonoFuncTS monoFuncTS -> monoFuncTS;
      default -> throw new IllegalArgumentException("Expected "
          + MonoFuncTS.class.getCanonicalName() + " got " + func.getClass().getCanonicalName()
          + ".");
    };
  }

  private static void checkAssignment(MonoTS source, MonoTS target, Side side) {
    if (!(source.equals(target)
        || source.equals(edgeTS(side.other()))
        || target.equals(edgeTS(side)))) {
      throw new IllegalArgumentException(
          "Types do not match: " + source.q() + " and " + target.q() + ".");
    }
  }

  private static MonoTS reducedBounds(Entry<VarS, Bounds<Set<MonoTS>>> entry) {
    var bounds = entry.getValue();
    if (bounds.lower().isEmpty()) {
      return resolveBound(bounds, UPPER);
    }
    if (bounds.upper().isEmpty()) {
      return resolveBound(bounds, LOWER);
    }
    MonoTS upper = resolveBound(bounds, UPPER);
    MonoTS lower = resolveBound(bounds, LOWER);
    ConstrS constr = constrS(lower, upper);

    try {
      var constrs = decompose(constr);
      if (!constrs.isEmpty()) {
        throw new IllegalArgumentException(
            ("Deduced bounds (%s) for variable %s are not legal. They generate additional "
                + "constraints %s.")
                .formatted(constr, entry.getKey(), constrs));
      }
    } catch (ConstrDecomposeExc e) {
      throw new IllegalArgumentException(
          "Deduced bounds for variable %s are not legal. lower < upper is false: %s."
              .formatted(entry.getKey().q(), constr));
    }
    return upper;
  }

  private static MonoTS resolveBound(Bounds<Set<MonoTS>> bounds, Side upper) {
    return TYPING.resolveMerge(bounds.get(upper), upper.other());
  }
}
