package org.smoothbuild.compilerfrontend.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.STempVar;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

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
  private final Map<SVar, Unified> varToUnified;
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

  public SType structureOf(SType type) {
    var tempMap = new HashMap<SType, SType>();
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
    var ordered = listOfAll(instantiationConstraints);
    while (!queue.isEmpty()) {
      var resolvedBefore = resolvedInstantiationConstraints(ordered);
      drainQueue(queue);
      var resolvedAfter = resolvedInstantiationConstraints(ordered);
      var updated = findUpdated(resolvedBefore, resolvedAfter, ordered);
      queue = new LinkedList<>(updated.map(this::toEqualityConstraint));
    }
  }

  private static List<InstantiationConstraint> findUpdated(
      List<ResolvedInstantiation> before,
      List<ResolvedInstantiation> after,
      List<InstantiationConstraint> ordered) {
    var builder = new ArrayList<InstantiationConstraint>();
    for (int i = 0; i < before.size(); i++) {
      if (!before.get(i).equals(after.get(i))) {
        builder.add(ordered.get(i));
      }
    }
    return listOfAll(builder);
  }

  private static record ResolvedInstantiation(SType instantiation, SType schema) {}

  private List<ResolvedInstantiation> resolvedInstantiationConstraints(
      List<InstantiationConstraint> orderedInstantiationConstraints) {
    return orderedInstantiationConstraints.map(this::resolve);
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
    if (type1 instanceof STempVar tempVar1) {
      if (type2 instanceof STempVar tempVar2) {
        unifyTempVarAndTempVar(tempVar1, tempVar2, queue);
      } else {
        unifyTempVarAndNonTempVar(tempVar1, type2, queue);
      }
    } else {
      if (type2 instanceof STempVar tempVar2) {
        unifyTempVarAndNonTempVar(tempVar2, type1, queue);
      } else {
        ConstraintInferrer.unifyAndInferConstraints(type1, type2, queue);
      }
    }
  }

  private void unifyTempVarAndTempVar(
      STempVar tempVar1, STempVar tempVar2, Queue<EqualityConstraint> constraints)
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
      destination.type =
          ConstraintInferrer.unifyAndInferConstraints(destination.type, source.type, constraints);
    } else {
      destination.type = destination.type != null ? destination.type : source.type;
    }
    failIfCycleExists(destination);
  }

  private void unifyTempVarAndNonTempVar(
      STempVar temp, SType type, Queue<EqualityConstraint> constraints) throws UnifierException {
    var unified = unifiedFor(temp);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unified.type = ConstraintInferrer.unifyAndInferConstraints(unified.type, type, constraints);
    }
    type.forEachTempVar(t -> unifiedFor(t).usedIn.add(unified));
    failIfCycleExists(unified);
  }

  public STempVar newTempVar() {
    var tempVar = tempVarGenerator.next();
    varToUnified.put(tempVar, new Unified(tempVar));
    return tempVar;
  }

  // resolving

  public SType resolve(SType sType) {
    return switch (sType) {
      case STempVar tempVar -> resolveTempVar(tempVar);
      default -> resolveNonTemp(sType);
    };
  }

  private SType resolveTempVar(STempVar tempVar) {
    var unified = unifiedFor(tempVar);
    if (unified.type == null) {
      return unified.mainVar;
    } else {
      return resolveNonTemp(unified.type);
    }
  }

  private SType resolveNonTemp(SType type) {
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

  private Unified unifiedFor(STempVar tempVar) {
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
    private final STempVar mainVar;
    private final Set<STempVar> vars;
    private final Set<Unified> usedIn;
    private SType type;

    public Unified(STempVar var) {
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
