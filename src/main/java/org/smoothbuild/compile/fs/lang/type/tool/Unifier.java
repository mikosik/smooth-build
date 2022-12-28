package org.smoothbuild.compile.fs.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static org.smoothbuild.util.Strings.q;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.TempVarS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;

import com.google.common.collect.Sets;

/**
 * Unifier allows unifying types and type parameters, so it is possible to infer types.
 *
 * Unifier treats differently VarS and TempVarS.
 * VarS represents type parameter that is fixed and any unification with type different
 * from itself causes error. For example VarS(A) can represent type of function parameter so
 * unifying it with VarS(B) (type of different parameter) is type error.
 * TempVarS is type variable that is unknown but can be inferred during unification. For example
 * TempVarS(1) can represent result type of function which doesn't specify its result type.
 * We can unify it with String and infer its type.
 */
public class Unifier {
  private final Map<VarS, Unified> varToUnified;
  private int tempVarCounter;

  public Unifier() {
    this.varToUnified = new HashMap<>();
    this.tempVarCounter = 0;
  }

  public void unifyOrFailWithRuntimeException(TypeS type1, TypeS type2) {
    try {
      unifyNormalized(normalize(type1), normalize(type2));
    } catch (UnifierExc e) {
      throw new RuntimeException(
          "unifyOrFailWithRuntimeException() caused exception. This means we have bug in code. "
          + "type1 = " + type1 + ", type2 = " + type2, e);
    }
  }

  public void unify(TypeS type1, TypeS type2) throws UnifierExc {
    unifyNormalized(normalize(type1), normalize(type2));
  }

  public TypeS resolve(TypeS type) {
    return denormalize(type);
  }

  public VarS newTempVar() {
    return newTempVar(null);
  }

  // unification

  private void unifyNormalized(TypeS normal1, TypeS normal2) throws UnifierExc {
    if (normal1 instanceof TempVarS tempVar1) {
      if (normal2 instanceof TempVarS tempVar2) {
        unifyNormalizedTempVarAndTempVar(tempVar1, tempVar2);
      } else {
        unifyNormalizedTempVarAndNonTempVar(tempVar1, normal2);
      }
    } else if (normal2 instanceof TempVarS tempVar2) {
      unifyNormalizedTempVarAndNonTempVar(tempVar2, normal1);
    } else {
      unifyNormalizedNonTempVars(normal1, normal2);
    }
  }

  private void unifyNormalizedTempVarAndTempVar(TempVarS tempVar1, TempVarS tempVar2)
      throws UnifierExc {
    var unified1 = unifiedFor(tempVar1);
    var unified2 = unifiedFor(tempVar2);
    if (unified1 != unified2) {
      unified1.vars.addAll(unified2.vars);
      unified2.vars.forEach(v -> varToUnified.put(v, unified1));
      var oldNormal1 = unified1.normal;
      unified1.normal = unified1.normal != null ? unified1.normal : unified2.normal;
      if (oldNormal1 != null && unified2.normal != null) {
        unifyNormalizedNonTempVars(oldNormal1, unified2.normal);
      }
      failIfCycleExists(unified1);
    }
  }

  private void unifyNormalizedTempVarAndNonTempVar(TempVarS var, TypeS normal) throws UnifierExc {
    var unified = unifiedFor(var);
    if (unified.normal == null) {
      unified.normal = normal;
    } else {
      unifyNormalizedNonTempVars(unified.normal, normal);
    }
    failIfCycleExists(unified);
  }

  private void unifyNormalizedNonTempVars(TypeS normal1, TypeS normal2) throws UnifierExc {
    switch (normal1) {
      case ArrayTS array1 -> unifyNormalizedArray(array1, normal2);
      case FuncTS func1 -> unifyNormalizedFunc(func1, normal2);
      case TupleTS tuple1 -> unifyNormalizedTuple(tuple1, normal2);
      case TempVarS tempVarS -> throw new RuntimeException("shouldn't happen");
      // default case also handles VarS
      default -> {
        if (!normal1.equals(normal2)) {
          throw new UnifierExc();
        }
      }
    }
  }

  private void unifyNormalizedArray(ArrayTS array1, TypeS normal2) throws UnifierExc {
    if (normal2 instanceof ArrayTS array2) {
      unifyNormalized(array1.elem(), array2.elem());
    } else {
      throw new UnifierExc();
    }
  }

  private void unifyNormalizedFunc(FuncTS func1, TypeS normal2) throws UnifierExc {
    if (normal2 instanceof FuncTS func2) {
      unifyNormalized(func1.res(), func2.res());
      var params1 = func1.params().items();
      var params2 = func2.params().items();
      if (params1.size() != params2.size()) {
        throw new UnifierExc();
      }
      for (int i = 0; i < params1.size(); i++) {
        unifyNormalized(params1.get(i), params2.get(i));
      }
    } else {
      throw new UnifierExc();
    }
  }

  private void unifyNormalizedTuple(TupleTS tuple1, TypeS normal2) throws UnifierExc {
    if (normal2 instanceof TupleTS tuple2) {
      var items1 = tuple1.items();
      var items2 = tuple2.items();
      if (items1.size() != items2.size()) {
        throw new UnifierExc();
      }
      for (int i = 0; i < items1.size(); i++) {
        unifyNormalized(items1.get(i), items2.get(i));
      }
    } else {
      throw new UnifierExc();
    }
  }

  // normalization

  private TypeS normalize(TypeS type) {
    return type.mapComponents(this::normalizeComponent);
  }

  private VarS normalizeComponent(TypeS type) {
    if (type instanceof TempVarS var) {
      return var;
    } else {
      return newTempVar(normalize(type));
    }
  }

  private TempVarS newTempVar(TypeS normal) {
    var var = new TempVarS(Integer.toString(tempVarCounter++));
    varToUnified.put(var, new Unified(var, normal));
    return var;
  }

  // denormalization

  private TypeS denormalize(TypeS normal) {
    return switch (normal) {
      case TempVarS tempVar -> denormalizeTempVar(tempVar);
      default -> denormalizeNormal(normal);
    };
  }

  private TypeS denormalizeTempVar(TempVarS tempVar) {
    var unified = unifiedFor(tempVar);
    if (unified.normal == null) {
      return unified.mainVar;
    } else {
      return denormalizeNormal(unified.normal);
    }
  }

  private TypeS denormalizeNormal(TypeS normal) {
    return normal.mapComponents(this::denormalize);
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
    if (unified.normal != null) {
      return unified.normal.vars().stream()
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
    private TypeS normal;

    public Unified(TempVarS var, TypeS normal) {
      this.mainVar = var;
      this.vars = new HashSet<>();
      this.vars.add(var);
      this.normal = normal;
    }

    @Override
    public String toString() {
      return "Unified{" + mainVar + ", " + vars + ", " + normal + '}';
    }
  }
}
