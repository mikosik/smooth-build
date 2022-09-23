package org.smoothbuild.compile.lang.type.tool;

import static java.util.stream.Collectors.toCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.Sets;

public class Unifier {
  private final UniqueVarGenerator uniqueVarGenerator;
  private final Map<VarS, Unified> varToUnified;

  public Unifier() {
    this.varToUnified = new HashMap<>();
    this.uniqueVarGenerator = new UniqueVarGenerator("u");
  }

  public void unify(TypeS type1, TypeS type2) throws UnifierExc {
    unifyNormalized(normalize(type1), normalize(type2));
  }

  public TypeS resolve(TypeS type) {
    return type.mapVars(this::denormalizeVar);
  }

  public void addVar(VarS var) {
    createUnifiedIfMissing(var);
  }

  public VarS generateUniqueVar() {
    VarS var = uniqueVarGenerator.generate();
    createUnifiedIfMissing(var);
    return var;
  }

  // unification

  private void unifyNormalized(TypeS normal1, TypeS normal2) throws UnifierExc {
    if (normal1 instanceof VarS var1) {
      if (normal2 instanceof VarS var2) {
        unifyNormalizedVarAndVar(var1, var2);
      } else {
        unifyNormalizedVarAndNonVar(var1, normal2);
      }
    } else if (normal2 instanceof VarS var2) {
      unifyNormalizedVarAndNonVar(var2, normal1);
    } else {
      unifyNormalizedNonVars(normal1, normal2);
    }
  }

  private void unifyNormalizedVarAndVar(VarS var1, VarS var2) throws UnifierExc {
    var unified1 = varToUnified.get(var1);
    var unified2 = varToUnified.get(var2);
    if (unified1 != unified2) {
      if (unified1.mainVar.hasPrefix() && !unified2.mainVar.hasPrefix()) {
        unified1.mainVar = unified2.mainVar;
      }
      unified1.vars.addAll(unified2.vars);
      unified2.vars.forEach(v -> varToUnified.put(v, unified1));
      var oldNormal1 = unified1.normal;
      unified1.normal = unified1.normal != null ? unified1.normal : unified2.normal;
      if (oldNormal1 != null && unified2.normal != null) {
        unifyNormalizedNonVars(oldNormal1, unified2.normal);
      }
      failIfCycleExists(unified1);
    }
  }

  private void unifyNormalizedVarAndNonVar(VarS var, TypeS normal) throws UnifierExc {
    Unified unified = varToUnified.get(var);
    if (unified.normal == null) {
      unified.normal = normal;
    } else {
      unifyNormalizedNonVars(unified.normal, normal);
    }
    failIfCycleExists(unified);
  }

  private void unifyNormalizedNonVars(TypeS normal1, TypeS normal2) throws UnifierExc {
    switch (normal1) {
      case ArrayTS array1 -> unifyNormalizedArray(array1, normal2);
      case FuncTS func1 -> unifyNormalizedFunc(func1, normal2);
      case TupleTS tuple1 -> unifyNormalizedTuple(tuple1, normal2);
      case VarS varS -> throw new RuntimeException("shouldn't happen");
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
    return switch (type) {
      case VarS var -> normalizeVar(var);
      default -> type.mapComponents(this::toVar);
    };
  }

  private VarS normalizeVar(VarS var) {
    createUnifiedIfMissing(var);
    return var;
  }

  private VarS toVar(TypeS type) {
    if (type instanceof VarS var) {
      createUnifiedIfMissing(var);
      return var;
    } else {
      var normal = normalize(type);
      var var = uniqueVarGenerator.generate();
      varToUnified.put(var, new Unified(var, normal));
      return var;
    }
  }

  private void createUnifiedIfMissing(VarS var) {
    varToUnified.computeIfAbsent(var, v -> new Unified(var, null));
  }

  // denormalization

  private TypeS denormalize(TypeS normal) {
    return switch (normal) {
      case VarS var -> denormalizeVar(var);
      default -> normal.mapComponents(this::denormalize);
    };
  }

  private TypeS denormalizeVar(VarS var) {
    Unified unified = varToUnified.get(var);
    if (unified == null) {
      throw new IllegalStateException("Unknown variable " + var + ".");
    }
    if (unified.normal == null) {
      return unified.mainVar;
    } else {
      return denormalize(unified.normal);
    }
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
          .map(varToUnified::get)
          .collect(toCollection(Sets::newIdentityHashSet));
    } else {
      return Set.of();
    }
  }

  private static class Unified {
    private VarS mainVar;
    private final Set<VarS> vars;
    private TypeS normal;

    public Unified(VarS var, TypeS normal) {
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
