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
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TempVarS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
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
    if (type1 instanceof TempVarS tempVar1) {
      if (type2 instanceof TempVarS tempVar2) {
        unifyTempVarAndTempVar(tempVar1, tempVar2);
      } else {
        unifyTempVarAndNonTempVar(tempVar1, type2);
      }
    } else if (type2 instanceof TempVarS tempVar2) {
      unifyTempVarAndNonTempVar(tempVar2, type1);
    } else {
      unifyNonTempVars(type1, type2);
    }
  }

  private void unifyTempVarAndTempVar(TempVarS tempVar1, TempVarS tempVar2)
      throws UnifierExc {
    var unified1 = unifiedFor(tempVar1);
    var unified2 = unifiedFor(tempVar2);
    if (unified1 != unified2) {
      unified1.vars.addAll(unified2.vars);
      unified2.vars.forEach(v -> varToUnified.put(v, unified1));
      var oldType1 = unified1.type;
      unified1.type = unified1.type != null ? unified1.type : unified2.type;
      if (oldType1 != null && unified2.type != null) {
        unifyNonTempVars(oldType1, unified2.type);
      }
      failIfCycleExists(unified1);
    }
  }

  private void unifyTempVarAndNonTempVar(TempVarS var, TypeS type) throws UnifierExc {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unifyNonTempVars(unified.type, type);
    }
    failIfCycleExists(unified);
  }

  private void unifyNonTempVars(TypeS type1, TypeS type2) throws UnifierExc {
    switch (type1) {
      case ArrayTS array1 -> unifyArrays(array1, type2);
      case FuncTS func1 -> unifyFunctions(func1, type2);
      case TupleTS tuple1 -> unifyTuples(tuple1, type2);
      case StructTS structTS -> unifyStructs(structTS, type2);
      case TempVarS tempVarS -> throw new RuntimeException("shouldn't happen");
      // default case also handles VarS
      default -> {
        if (!type1.equals(type2)) {
          throw new UnifierExc();
        }
      }
    }
  }

  private void unifyArrays(ArrayTS array1, TypeS type2) throws UnifierExc {
    if (type2 instanceof ArrayTS array2) {
      unify(array1.elem(), array2.elem());
    } else {
      throw new UnifierExc();
    }
  }

  private void unifyFunctions(FuncTS func1, TypeS type2) throws UnifierExc {
    if (type2 instanceof FuncTS func2) {
      unify(func1.res(), func2.res());
      var params1 = func1.params().items();
      var params2 = func2.params().items();
      if (params1.size() != params2.size()) {
        throw new UnifierExc();
      }
      for (int i = 0; i < params1.size(); i++) {
        unify(params1.get(i), params2.get(i));
      }
    } else {
      throw new UnifierExc();
    }
  }

  private void unifyTuples(TupleTS tuple1, TypeS type2) throws UnifierExc {
    if (type2 instanceof TupleTS tuple2) {
      var items1 = tuple1.items();
      var items2 = tuple2.items();
      if (items1.size() != items2.size()) {
        throw new UnifierExc();
      }
      for (int i = 0; i < items1.size(); i++) {
        unify(items1.get(i), items2.get(i));
      }
    } else {
      throw new UnifierExc();
    }
  }

  private void unifyStructs(StructTS struct1, TypeS type2) throws UnifierExc {
    if (type2 instanceof StructTS struct2) {
      if (!struct1.name().equals(struct2.name())) {
        throw new UnifierExc();
      }
      var items1 = struct1.fields();
      var items2 = struct2.fields();
      if (items1.size() != items2.size()) {
        throw new UnifierExc();
      }
      for (int i = 0; i < items1.size(); i++) {
        if (!items1.get(i).name().equals(items2.get(i).name())) {
          throw new UnifierExc();
        }
      }
      for (int i = 0; i < items1.size(); i++) {
        unify(items1.get(i).type(), items2.get(i).type());
      }
    } else {
      throw new UnifierExc();
    }
  }

  public VarS newTempVar() {
    return newTempVar(null);
  }

  private TempVarS newTempVar(TypeS type) {
    var var = new TempVarS(Integer.toString(tempVarCounter++));
    varToUnified.put(var, new Unified(var, type));
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
    return type.mapComponents(this::resolve);
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

    public Unified(TempVarS var, TypeS type) {
      this.mainVar = var;
      this.vars = new HashSet<>();
      this.vars.add(var);
      this.type = type;
    }

    @Override
    public String toString() {
      return "Unified{" + mainVar + ", " + vars + ", " + type + '}';
    }
  }
}
