package org.smoothbuild.compile.fs.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static org.smoothbuild.compile.fs.lang.type.tool.ConstraintInferrer.unifyAndInferConstraints;
import static org.smoothbuild.util.Strings.q;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.smoothbuild.compile.fs.lang.type.TempVarS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;

import com.google.common.collect.Sets;

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
  private int tempVarCounter;

  public Unifier() {
    this.varToUnified = new HashMap<>();
    this.tempVarCounter = 0;
  }

  // unification

  public void unifyOrFailWithRuntimeException(TypeS type1, TypeS type2) {
    try {
      unify(type1, type2);
    } catch (UnifierExc e) {
      throw new RuntimeException(
          "unifyOrFailWithRuntimeException() caused exception. This means we have bug in code. "
              + "type1 = " + type1 + ", type2 = " + type2, e);
    }
  }

  public void unify(TypeS type1, TypeS type2) throws UnifierExc {
    Queue<Constraint> constraints = new LinkedList<>();
    constraints.add(new Constraint(type1, type2));
    drainQueue(constraints);
  }

  private void drainQueue(Queue<Constraint> queue) throws UnifierExc {
    while (!queue.isEmpty()) {
      var constraint = queue.remove();
      unify(constraint.type1(), constraint.type2(), queue);
    }
  }

  public void unify(TypeS type1, TypeS type2, Queue<Constraint> constraints) throws UnifierExc {
    if (type1 instanceof TempVarS tempVar1) {
      if (type2 instanceof TempVarS tempVar2) {
        unifyTempVarAndTempVar(tempVar1, tempVar2, constraints);
      } else {
        unifyTempVarAndNonTempVar(tempVar1, type2, constraints);
      }
    } else if (type2 instanceof TempVarS tempVar2) {
      unifyTempVarAndNonTempVar(tempVar2, type1, constraints);
    } else {
      unifyAndInferConstraints(type1, type2, constraints);
    }
  }

  private void unifyTempVarAndTempVar(
      TempVarS tempVar1, TempVarS tempVar2, Queue<Constraint> constraints) throws UnifierExc {
    var unified1 = unifiedFor(tempVar1);
    var unified2 = unifiedFor(tempVar2);
    if (unified1 != unified2) {
      unified1.vars.addAll(unified2.vars);
      unified2.vars.forEach(v -> varToUnified.put(v, unified1));
      if (unified1.type != null && unified2.type != null) {
        unified1.type = unifyAndInferConstraints(unified1.type, unified2.type, constraints);
      } else {
        unified1.type = unified1.type != null ? unified1.type : unified2.type;
      }
      failIfCycleExists(unified1);
    }
  }

  private void unifyTempVarAndNonTempVar(TempVarS var, TypeS type, Queue<Constraint> constraints)
      throws UnifierExc {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unified.type = unifyAndInferConstraints(unified.type, type, constraints);
    }
    failIfCycleExists(unified);
  }

  public VarS newTempVar() {
    var var = new TempVarS(Integer.toString(tempVarCounter++));
    varToUnified.put(var, new Unified(var));
    return var;
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

  private void failIfCycleExists(Unified unified) throws UnifierExc {
    failIfCycleExists(new HashSet<>(), unified);
  }

  private void failIfCycleExists(HashSet<Unified> visited, Unified unified) throws UnifierExc {
    if (visited.add(unified)) {
      for (Unified u : referencedBy(unified)) {
        failIfCycleExists(visited, u);
      }
      visited.remove(unified);
    } else {
      throw new UnifierExc();
    }
  }

  private Set<Unified> referencedBy(Unified unified) {
    if (unified.type != null) {
      return unified.type.vars().stream()
          .filter(VarS::isTemporary)
          .map(varToUnified::get)
          .collect(toCollection(Sets::newIdentityHashSet));
    } else {
      return Set.of();
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
    private TypeS type;

    public Unified(TempVarS var) {
      this.mainVar = var;
      this.vars = new HashSet<>();
      this.vars.add(var);
      this.type = null;
    }

    @Override
    public String toString() {
      return "Unified{" + mainVar + ", " + vars + ", " + type + '}';
    }
  }
}
