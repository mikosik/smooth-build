package org.smoothbuild.compile.fs.lang.type.tool;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FieldSetTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.InterfaceTS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TempVarS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.util.collect.Lists;
import org.smoothbuild.util.function.ThrowingBiFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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

  public TypeS unify(TypeS type1, TypeS type2) throws UnifierExc {
    if (type1 instanceof TempVarS tempVar1) {
      if (type2 instanceof TempVarS tempVar2) {
        return unifyTempVarAndTempVar(tempVar1, tempVar2);
      } else {
        return unifyTempVarAndNonTempVar(tempVar1, type2);
      }
    } else if (type2 instanceof TempVarS tempVar2) {
      return unifyTempVarAndNonTempVar(tempVar2, type1);
    } else {
      return unifyNonTempVars(type1, type2);
    }
  }

  private TypeS unifyTempVarAndTempVar(TempVarS tempVar1, TempVarS tempVar2)
      throws UnifierExc {
    var unified1 = unifiedFor(tempVar1);
    var unified2 = unifiedFor(tempVar2);
    if (unified1 != unified2) {
      unified1.vars.addAll(unified2.vars);
      unified2.vars.forEach(v -> varToUnified.put(v, unified1));
      if (unified1.type != null && unified2.type != null) {
        unified1.type = unifyNonTempVars(unified1.type, unified2.type);
      } else {
        unified1.type = unified1.type != null ? unified1.type : unified2.type;
      }
      failIfCycleExists(unified1);
    }
    return unified1.mainVar;
  }

  private TypeS unifyTempVarAndNonTempVar(TempVarS var, TypeS type) throws UnifierExc {
    var unified = unifiedFor(var);
    if (unified.type == null) {
      unified.type = type;
    } else {
      unified.type = unifyNonTempVars(unified.type, type);
    }
    failIfCycleExists(unified);
    return unified.mainVar;
  }

  private TypeS unifyNonTempVars(TypeS type1, TypeS type2) throws UnifierExc {
    return switch (type1) {
      // @formatter:off
      case ArrayTS     array1     -> unifyArrayAndType(array1, type2);
      case FieldSetTS  fieldSet1  -> unifyFieldSetAndType(fieldSet1, type2);
      case FuncTS      func1      -> unifyFunctionAndType(func1, type2);
      case TupleTS     tuple1     -> unifyTupleAndType(tuple1, type2);
      case TempVarS    tempVarS   -> throw new RuntimeException("shouldn't happen");
      case VarS        varS       -> assertTypesAreEqual(varS, type2);
      default                     -> assertTypesAreEqual(type1, type2);
      // @formatter:on
    };
  }

  private TypeS assertTypesAreEqual(TypeS type1, TypeS type2) throws UnifierExc {
    if (type1.equals(type2)) {
      return type1;
    } else {
      throw new UnifierExc();
    }
  }

  private ArrayTS unifyArrayAndType(ArrayTS array1, TypeS type2) throws UnifierExc {
    if (type2 instanceof ArrayTS array2) {
      return new ArrayTS(unify(array1.elem(), array2.elem()));
    } else {
      throw new UnifierExc();
    }
  }

  private FieldSetTS unifyFieldSetAndType(FieldSetTS fieldSet, TypeS type) throws UnifierExc {
    return switch (type) {
      case InterfaceTS interfaceTS -> unifyFieldSetAndInterface(fieldSet, interfaceTS);
      case StructTS structTS -> unifyFieldSetAndStruct(fieldSet, structTS);
      default -> throw new UnifierExc();
    };
  }

  private FieldSetTS unifyFieldSetAndInterface(FieldSetTS fieldSet, InterfaceTS interface2)
      throws UnifierExc {
    return switch (fieldSet) {
      case InterfaceTS interface1 -> new InterfaceTS(unifyFieldSetAndFieldSet(interface1, interface2));
      case StructTS struct1 -> unifyStructAndInterface(struct1, interface2);
    };
  }

  private StructTS unifyFieldSetAndStruct(FieldSetTS fieldSet, StructTS struct2) throws UnifierExc {
    return switch (fieldSet) {
      case InterfaceTS interface1 -> unifyStructAndInterface(struct2, interface1);
      case StructTS struct1 -> unifyStructAndStruct(struct1, struct2);
    };
  }

  private StructTS unifyStructAndInterface(StructTS struct, InterfaceTS interface_)
      throws UnifierExc {
    var unifiedFields = unifyFieldSetAndFieldSet(struct, interface_);
    if (unifiedFields.size() != struct.fields().size()) {
      throw new UnifierExc();
    } else {
      return struct;
    }
  }

  private ImmutableMap<String, ItemSigS> unifyFieldSetAndFieldSet(
      FieldSetTS fieldSet1, FieldSetTS fieldSet2) throws UnifierExc {
    var fields1 = fieldSet1.fieldSet();
    var fields2 = fieldSet2.fieldSet();
    var mergedFields = new HashMap<>(fields1);
    for (Entry<String, ItemSigS> field2 : fields2.entrySet()) {
      var name = field2.getKey();
      var field1 = mergedFields.get(name);
      if (field1 == null) {
        mergedFields.put(name, field2.getValue());
      } else {
        mergedFields.put(name, new ItemSigS(unify(field1.type(), field2.getValue().type()), name));
      }
    }
    return ImmutableMap.copyOf(mergedFields);
  }

  private StructTS unifyStructAndStruct(StructTS struct1, StructTS struct2) throws UnifierExc {
    if (!struct1.name().equals(struct2.name())) {
      throw new UnifierExc();
    }
    var items1 = struct1.fields();
    var items2 = struct2.fields();
    return new StructTS(struct1.name(), nlist(zip(items1, items2, this::unifyItemSigAndItemSig)));
  }

  private ItemSigS unifyItemSigAndItemSig(ItemSigS itemSig1, ItemSigS itemSig2) throws UnifierExc {
    var name1 = itemSig1.name();
    var name2 = itemSig2.name();
    if (!name1.equals(name2)) {
      throw new UnifierExc();
    }
    var type1 = itemSig1.type();
    var type2 = itemSig2.type();
    return new ItemSigS(unify(type1, type2), name1);
  }

  private FuncTS unifyFunctionAndType(FuncTS func1, TypeS type) throws UnifierExc {
    if (type instanceof FuncTS func2) {
      var result1 = func1.res();
      var result2 = func2.res();
      var result = unify(result1, result2);
      var params1 = func1.params();
      var params2 = func2.params();
      var params = unifyTupleAndTuple(params1, params2);
      return new FuncTS(params, result);
    } else {
      throw new UnifierExc();
    }
  }

  private TupleTS unifyTupleAndType(TupleTS tuple1, TypeS type) throws UnifierExc {
    if (type instanceof TupleTS tuple2) {
      return unifyTupleAndTuple(tuple1, tuple2);
    } else {
      throw new UnifierExc();
    }
  }

  private TupleTS unifyTupleAndTuple(TupleTS tuple1, TupleTS tuple2) throws UnifierExc {
    var items1 = tuple1.items();
    var items2 = tuple2.items();
    return new TupleTS(zip(items1, items2, this::unify));
  }

  private <T> ImmutableList<T> zip(
      List<T> items1, List<T> items2, ThrowingBiFunction<T, T, T, UnifierExc> mapper)
      throws UnifierExc {
    if (items1.size() != items2.size()) {
      throw new UnifierExc();
    }
    return Lists.zip(items1, items2, mapper);
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
