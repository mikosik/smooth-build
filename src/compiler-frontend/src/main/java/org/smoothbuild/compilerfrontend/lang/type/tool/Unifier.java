package org.smoothbuild.compilerfrontend.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.compilerfrontend.lang.type.tool.ConstraintInferrer.unifyAndInferConstraints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

/**
 * Unifier allows unifying types (`TypeS`s)
 * and type variables (`Var`s), so it is possible to infer types.
 * <p>
 * Unifier treats differently VarS that are flexible (`isFlexibleVar()` returns true) and VarS
 * that are rigid (`isFlexibleVar()` returns false). Rigid var represents type variable that is
 * fixed and any unification with type different from itself causes error. For example `A` can
 * represent type of function parameter so unifying it with B (type of different parameter) is
 * a type error. Flexible var represents unknown type that can be inferred during unification.
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
  private final Map<SVar, Unified> flexibleVarToUnified;
  private int flexibleVarCounter = 0;

  public Unifier() {
    this.flexibleVarToUnified = new HashMap<>();
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
    if (type1.isFlexibleVar()) {
      if (type2.isFlexibleVar()) {
        unifyFlexibleVarAndFlexibleVar((SVar) type1, (SVar) type2, queue);
      } else {
        unifyFlexibleVarAndNonFlexibleVar((SVar) type1, type2, queue);
      }
    } else {
      if (type2.isFlexibleVar()) {
        unifyFlexibleVarAndNonFlexibleVar((SVar) type2, type1, queue);
      } else {
        unifyAndInferConstraints(type1, type2, queue);
      }
    }
  }

  private void unifyFlexibleVarAndFlexibleVar(SVar var1, SVar var2, Queue<Constraint> constraints)
      throws UnifierException {
    var unified1 = unifiedFor(var1);
    var unified2 = unifiedFor(var2);
    if (unified1 != unified2) {
      mergeUnifiedGroups(unified1, unified2, constraints);
    }
  }

  private void mergeUnifiedGroups(
      Unified source, Unified destination, Queue<Constraint> constraints) throws UnifierException {
    var sourceSmaller = source.vars.size() + source.usedIn.size()
        < destination.vars.size() + destination.usedIn.size();
    var s = sourceSmaller ? source : destination;
    var d = sourceSmaller ? destination : source;

    d.vars.addAll(s.vars);
    d.usedIn.addAll(s.usedIn);
    s.vars.forEach(v -> flexibleVarToUnified.put(v, d));
    if (d.type != null && s.type != null) {
      d.type = unifyAndInferConstraints(d.type, s.type, constraints);
    } else {
      d.type = d.type != null ? d.type : s.type;
    }
    failIfCycleExists(d);
  }

  private void unifyFlexibleVarAndNonFlexibleVar(
      SVar var, SType type, Queue<Constraint> constraints) throws UnifierException {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unified.type = unifyAndInferConstraints(unified.type, type, constraints);
    }
    type.forEachFlexibleVar(t -> unifiedFor(t).usedIn.add(unified));
    failIfCycleExists(unified);
  }

  public SVar newFlexibleVar() {
    var var = SVar.newFlexibleVar(flexibleVarCounter++);
    flexibleVarToUnified.put(var, new Unified(var));
    return var;
  }

  // resolving

  public SType resolve(SType sType) {
    if (sType.isFlexibleVar()) {
      return resolveFlexibleVar((SVar) sType);
    } else {
      return resolveNonFlexible(sType);
    }
  }

  private SType resolveFlexibleVar(SVar var) {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      return unified.mainVar;
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

  private Unified unifiedFor(SVar var) {
    var unified = flexibleVarToUnified.get(var);
    if (unified == null) {
      throw new IllegalStateException("Unknown flexible var " + q(var.name()) + ".");
    }
    return unified;
  }

  @Override
  public String toString() {
    return new HashSet<>(flexibleVarToUnified.values())
        .stream()
            .sorted(comparing(u -> u.mainVar.name()))
            .map(Object::toString)
            .collect(joining("\n"));
  }

  private static class Unified {
    private final SVar mainVar;
    private final Set<SVar> vars;
    private final Set<Unified> usedIn;
    private SType type;

    public Unified(SVar var) {
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
