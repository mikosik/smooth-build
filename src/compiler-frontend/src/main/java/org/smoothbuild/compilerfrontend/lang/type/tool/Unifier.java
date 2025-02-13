package org.smoothbuild.compilerfrontend.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.flexibleTypeVar;
import static org.smoothbuild.compilerfrontend.lang.type.tool.ConstraintInferrer.unifyAndInferConstraints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

/**
 * Unifier allows unifying types (`TypeS`s)
 * and type variables (`TypeVar`s), so it is possible to infer types.
 * <p>
 * Unifier treats differently TypeVar-s that are flexible (`isFlexibleTypeVar()` returns true) and
 * TypeVarS that are rigid (`isFlexibleTypeVar()` returns false).
 * Rigid TypeVar represents type variable that is
 * fixed and any unification with type different from itself causes error. For example `A` can
 * represent type of function parameter so unifying it with B (type of different parameter) is
 * a type error. Flexible TypeVar represents unknown type that can be inferred during unification.
 * For example _1 can represent result type of function which doesn't specify its result type.
 * We can unify it with String or A and infer its type this way.
 * <p>
 * InterfaceTS describes type by enumerating some (possibly all) of its fields.
 * Unifying such field with other InterfaceTS or StructTS is handled slightly
 * differently than other types. For example unifying some flexible var with
 * Interface(Int field1) and then with Interface(Int field2) won't cause
 * type error (as it would in case of unifying two structs) but just merge
 * both interfaces inferring type of flexible var as Interface(Int field1, Int field2).
 */
public class Unifier {
  private final Map<STypeVar, Unified> flexibleTypeVarToUnified;
  private int flexibleVarCounter = 0;

  public Unifier() {
    this.flexibleTypeVarToUnified = new HashMap<>();
  }

  public void addOrFailWithRuntimeException(Constraint constraint) {
    try {
      add(constraint);
    } catch (UnifierException e) {
      throw new RuntimeException(
          "addOrFailWithRuntimeException() caused exception. This means we have bug in code. "
              + "constraint = " + constraint + ".",
          e);
    }
  }

  public void add(Constraint constraint) throws UnifierException {
    var queue = new LinkedList<Constraint>();
    queue.add(constraint);
    drainQueue(queue);
  }

  private void drainQueue(LinkedList<Constraint> queue) throws UnifierException {
    while (!queue.isEmpty()) {
      unify(queue.remove(), queue);
    }
  }

  private void unify(Constraint constraint, Queue<Constraint> queue) throws UnifierException {
    var type1 = constraint.type1();
    var type2 = constraint.type2();
    if (type1.isFlexibleTypeVar()) {
      if (type2.isFlexibleTypeVar()) {
        unifyFlexibleVarAndFlexibleVar((STypeVar) type1, (STypeVar) type2, queue);
      } else {
        unifyFlexibleVarAndNonFlexibleVar((STypeVar) type1, type2, queue);
      }
    } else {
      if (type2.isFlexibleTypeVar()) {
        unifyFlexibleVarAndNonFlexibleVar((STypeVar) type2, type1, queue);
      } else {
        unifyAndInferConstraints(type1, type2, queue);
      }
    }
  }

  private void unifyFlexibleVarAndFlexibleVar(
      STypeVar var1, STypeVar var2, Queue<Constraint> constraints) throws UnifierException {
    var unified1 = unifiedFor(var1);
    var unified2 = unifiedFor(var2);
    if (unified1 != unified2) {
      mergeUnifiedGroups(unified1, unified2, constraints);
    }
  }

  private void mergeUnifiedGroups(
      Unified source, Unified destination, Queue<Constraint> constraints) throws UnifierException {
    var sourceSmaller = source.typeVars.size() + source.usedIn.size()
        < destination.typeVars.size() + destination.usedIn.size();
    var s = sourceSmaller ? source : destination;
    var d = sourceSmaller ? destination : source;

    d.typeVars.addAll(s.typeVars);
    d.usedIn.addAll(s.usedIn);
    s.typeVars.forEach(v -> flexibleTypeVarToUnified.put(v, d));
    if (d.type != null && s.type != null) {
      d.type = unifyAndInferConstraints(d.type, s.type, constraints);
    } else {
      d.type = d.type != null ? d.type : s.type;
    }
    failIfCycleExists(d);
  }

  private void unifyFlexibleVarAndNonFlexibleVar(
      STypeVar var, SType type, Queue<Constraint> constraints) throws UnifierException {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unified.type = unifyAndInferConstraints(unified.type, type, constraints);
    }
    type.forEachFlexibleVar(t -> unifiedFor(t).usedIn.add(unified));
    failIfCycleExists(unified);
  }

  public STypeVar newFlexibleVar() {
    var var = flexibleTypeVar(flexibleVarCounter++);
    flexibleTypeVarToUnified.put(var, new Unified(var));
    return var;
  }

  // resolving

  public SType resolve(SType sType) {
    if (sType.isFlexibleTypeVar()) {
      return resolveFlexibleVar((STypeVar) sType);
    } else {
      return resolveNonFlexible(sType);
    }
  }

  private SType resolveFlexibleVar(STypeVar var) {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      return unified.mainTypeVar;
    } else {
      return resolveNonFlexible(unified.type);
    }
  }

  private SType resolveNonFlexible(SType type) {
    return type.mapFlexibleVars(this::resolve);
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

  private Unified unifiedFor(STypeVar var) {
    var unified = flexibleTypeVarToUnified.get(var);
    if (unified == null) {
      throw new IllegalStateException("Unknown flexible type var " + var.fqn().q() + ".");
    }
    return unified;
  }

  @Override
  public String toString() {
    return new HashSet<>(flexibleTypeVarToUnified.values())
        .stream()
            .sorted(comparing(u -> u.mainTypeVar.fqn()))
            .map(Object::toString)
            .collect(joining("\n"));
  }

  private static class Unified {
    private final STypeVar mainTypeVar;
    private final Set<STypeVar> typeVars;
    private final Set<Unified> usedIn;
    private SType type;

    public Unified(STypeVar var) {
      this.mainTypeVar = var;
      this.typeVars = new HashSet<>();
      this.usedIn = new HashSet<>();
      this.typeVars.add(var);
      this.type = null;
    }

    @Override
    public String toString() {
      return "Unified{" + mainTypeVar + ", " + typeVars + ", " + type + '}';
    }
  }
}
