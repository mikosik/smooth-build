package org.smoothbuild.compile.frontend.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.Strings.q;
import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.compile.frontend.lang.type.tool.ConstraintInferrer.unifyAndInferConstraints;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.smoothbuild.compile.frontend.lang.type.TempVarS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.compile.frontend.lang.type.VarS;

/**
 * Unifier allows unifying types (`TypeS`s)
 * and type parameters (`Var`s), so it is possible to infer types.
 *
 * Unifier treats differently VarS and TempVarS.
 * VarS represents type parameter that is fixed and any unification with type different
 * from itself causes error. For example VarS(A) can represent type of function parameter so
 * unifying it with VarS(B) (type of different parameter) is type error.
 * TempVarS is type variable that is unknown but can be inferred during unification. For example
 * TempVarS(1) can represent result type of function which doesn't specify its result type.
 * We can unify it with String and infer its type.
 *
 * InterfaceTS describes type by enumerating some (possibly all) of its fields.
 * Unifying such field with other InterfaceTS or StructTS is handled slightly
 * differently than other types. For example unifying some TempVar with
 * Interface(Int field1) and then with Interface(Int field2) won't cause
 * type error (as it would in case of unifying two structs) but just merge
 * both interfaces inferring type of TempVar as Interface(Int field1, Int field2).
 */
public class Unifier {
  private final Map<VarS, Unified> varToUnified;
  private final Set<InstantiationConstraint> instantiationConstraints;
  private final TempVarGenerator tempVarGenerator;

  public Unifier() {
    this.varToUnified = new HashMap<>();
    this.instantiationConstraints = new HashSet<>();
    this.tempVarGenerator = new TempVarGenerator();
  }

  public void add(Constraint constraint) throws UnifierException {
    switch (constraint) {
      case EqualityConstraint equality -> add(equality);
      case InstantiationConstraint instatiation -> add(instatiation);
    }
  }

  // instantiation constraint

  public void add(InstantiationConstraint constraint) throws UnifierException {
    instantiationConstraints.add(constraint);
    add(toEqualityConstraint(constraint));
  }

  private EqualityConstraint toEqualityConstraint(InstantiationConstraint constraint) {
    return new EqualityConstraint(constraint.instantiation(), structureOf(constraint.schema()));
  }

  public TypeS structureOf(TypeS type) {
    var tempMap = new HashMap<TypeS, TypeS>();
    return resolve(type).mapTemps((temp) -> tempMap.computeIfAbsent(temp, t -> newTempVar()));
  }

  // equality constraint

  public void addOrFailWithRuntimeException(EqualityConstraint constraint) {
    try {
      add(constraint);
    } catch (UnifierException e) {
      throw new RuntimeException(
          "addOrFailWithRuntimeException() caused exception. This means we have bug in code. "
              + "constraint = " + constraint + ".",
          e);
    }
  }

  public void add(EqualityConstraint constraint) throws UnifierException {
    var queue = new LinkedList<EqualityConstraint>();
    queue.add(constraint);
    var ordered = ImmutableList.copyOf(instantiationConstraints);
    while (!queue.isEmpty()) {
      var resolvedBefore = resolvedInstantiationConstraints(ordered);
      drainQueue(queue);
      var resolvedAfter = resolvedInstantiationConstraints(ordered);
      var updated = findUpdated(resolvedBefore, resolvedAfter, ordered);
      queue = new LinkedList<>(map(updated, this::toEqualityConstraint));
    }
  }

  private static ImmutableList<InstantiationConstraint> findUpdated(
      List<ResolvedInstantiation> before,
      List<ResolvedInstantiation> after,
      List<InstantiationConstraint> ordered) {
    Builder<InstantiationConstraint> builder = ImmutableList.builder();
    for (int i = 0; i < before.size(); i++) {
      if (!before.get(i).equals(after.get(i))) {
        builder.add(ordered.get(i));
      }
    }
    return builder.build();
  }

  private static record ResolvedInstantiation(TypeS instantiation, TypeS schema) {}

  private List<ResolvedInstantiation> resolvedInstantiationConstraints(
      List<InstantiationConstraint> orderedInstantiationConstraints) {
    return map(orderedInstantiationConstraints, this::resolve);
  }

  private ResolvedInstantiation resolve(InstantiationConstraint constraint) {
    return new ResolvedInstantiation(
        resolve(constraint.instantiation()), resolve(constraint.schema()));
  }

  private void drainQueue(LinkedList<EqualityConstraint> queue) throws UnifierException {
    while (!queue.isEmpty()) {
      unify(queue.remove(), queue);
    }
  }

  private void unify(EqualityConstraint constraint, Queue<EqualityConstraint> queue)
      throws UnifierException {
    var type1 = constraint.type1();
    var type2 = constraint.type2();
    if (type1 instanceof TempVarS tempVar1) {
      if (type2 instanceof TempVarS tempVar2) {
        unifyTempVarAndTempVar(tempVar1, tempVar2, queue);
      } else {
        unifyTempVarAndNonTempVar(tempVar1, type2, queue);
      }
    } else {
      if (type2 instanceof TempVarS tempVar2) {
        unifyTempVarAndNonTempVar(tempVar2, type1, queue);
      } else {
        unifyAndInferConstraints(type1, type2, queue);
      }
    }
  }

  private void unifyTempVarAndTempVar(
      TempVarS tempVar1, TempVarS tempVar2, Queue<EqualityConstraint> constraints)
      throws UnifierException {
    var unified1 = unifiedFor(tempVar1);
    var unified2 = unifiedFor(tempVar2);
    if (unified1 != unified2) {
      if (unified1.mainVar.isOlderThan(unified2.mainVar)) {
        mergeUnifiedGroups(unified1, unified2, constraints);
      } else {
        mergeUnifiedGroups(unified2, unified1, constraints);
      }
    }
  }

  private void mergeUnifiedGroups(
      Unified destination, Unified source, Queue<EqualityConstraint> constraints)
      throws UnifierException {
    destination.vars.addAll(source.vars);
    destination.usedIn.addAll(source.usedIn);
    source.vars.forEach(v -> varToUnified.put(v, destination));
    if (destination.type != null && source.type != null) {
      destination.type = unifyAndInferConstraints(destination.type, source.type, constraints);
    } else {
      destination.type = destination.type != null ? destination.type : source.type;
    }
    failIfCycleExists(destination);
  }

  private void unifyTempVarAndNonTempVar(
      TempVarS temp, TypeS type, Queue<EqualityConstraint> constraints) throws UnifierException {
    var unified = unifiedFor(temp);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unified.type = unifyAndInferConstraints(unified.type, type, constraints);
    }
    type.forEachTempVar(t -> unifiedFor(t).usedIn.add(unified));
    failIfCycleExists(unified);
  }

  public TempVarS newTempVar() {
    var tempVar = tempVarGenerator.next();
    varToUnified.put(tempVar, new Unified(tempVar));
    return tempVar;
  }

  // resolving

  public TypeS resolve(TypeS typeS) {
    return switch (typeS) {
      case TempVarS tempVar -> resolveTempVar(tempVar);
      default -> resolveNonTemp(typeS);
    };
  }

  private TypeS resolveTempVar(TempVarS tempVar) {
    var unified = unifiedFor(tempVar);
    if (unified.type == null) {
      return unified.mainVar;
    } else {
      return resolveNonTemp(unified.type);
    }
  }

  private TypeS resolveNonTemp(TypeS type) {
    return type.mapTemps(this::resolve);
  }

  // Unified helpers

  private void failIfCycleExists(Unified unified) throws UnifierException {
    failIfCycleExists(new HashSet<>(), unified);
  }

  private void failIfCycleExists(HashSet<Unified> visited, Unified unified)
      throws UnifierException {
    if (visited.add(unified)) {
      for (Unified u : unified.usedIn) {
        failIfCycleExists(visited, u);
      }
      visited.remove(unified);
    } else {
      throw new UnifierException();
    }
  }

  private Unified unifiedFor(TempVarS tempVar) {
    var unified = varToUnified.get(tempVar);
    if (unified == null) {
      throw new IllegalStateException("Unknown temp var " + q(tempVar.name()) + ".");
    }
    return unified;
  }

  @Override
  public String toString() {
    return new HashSet<>(varToUnified.values())
        .stream()
            .sorted(comparing(u -> u.mainVar.name()))
            .map(Object::toString)
            .collect(joining("\n"));
  }

  private static class Unified {
    private final TempVarS mainVar;
    private final Set<TempVarS> vars;
    private final Set<Unified> usedIn;
    private TypeS type;

    public Unified(TempVarS var) {
      this.mainVar = var;
      this.vars = new HashSet<>();
      this.usedIn = new HashSet<>();
      this.vars.add(var);
      this.type = null;
    }

    @Override
    public String toString() {
      return "Unified{" + mainVar + ", " + vars + ", " + type + '}';
    }
  }
}
