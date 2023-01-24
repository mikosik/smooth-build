package org.smoothbuild.compile.fs.lang.type.tool;

import static org.smoothbuild.util.collect.NList.nlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

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

public class ConstraintInferrer {
  public static TypeS unifyAndInferConstraints(
      TypeS type1, TypeS type2, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    if (type1 instanceof TempVarS) {
      constraints.add(new EqualityConstraint(type1, type2));
      return type1;
    }
    if (type2 instanceof TempVarS) {
      constraints.add(new EqualityConstraint(type1, type2));
      return type2;
    }
    return switch (type1) {
      // @formatter:off
      case ArrayTS     array1     -> unifyArrayAndType(array1, type2, constraints);
      case FieldSetTS  fieldSet1  -> unifyFieldSetAndType(fieldSet1, type2, constraints);
      case FuncTS      func1      -> unifyFunctionAndType(func1, type2, constraints);
      case TupleTS     tuple1     -> unifyTupleAndType(tuple1, type2, constraints);
      case VarS        varS       -> assertTypesAreEqual(varS, type2);
      default                     -> assertTypesAreEqual(type1, type2);
      // @formatter:on
    };
  }

  private static TypeS assertTypesAreEqual(TypeS type1, TypeS type2) throws UnifierExc {
    if (type1.equals(type2)) {
      return type1;
    } else {
      throw new UnifierExc();
    }
  }

  private static ArrayTS unifyArrayAndType(
      ArrayTS array1, TypeS type2, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    if (type2 instanceof ArrayTS array2) {
      return new ArrayTS(unifyAndInferConstraints(array1.elem(), array2.elem(), constraints));
    } else {
      throw new UnifierExc();
    }
  }

  private static FieldSetTS unifyFieldSetAndType(
      FieldSetTS fieldSet, TypeS type, Queue<EqualityConstraint> constraints) throws UnifierExc {
    return switch (type) {
      case InterfaceTS interfaceTS -> unifyFieldSetAndInterface(fieldSet, interfaceTS, constraints);
      case StructTS structTS -> unifyFieldSetAndStruct(fieldSet, structTS, constraints);
      default -> throw new UnifierExc();
    };
  }

  private static FieldSetTS unifyFieldSetAndInterface(
      FieldSetTS fieldSet, InterfaceTS interface2, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    return switch (fieldSet) {
      case InterfaceTS interface1 -> new InterfaceTS(
          unifyFieldSetAndFieldSet(interface1, interface2, constraints));
      case StructTS struct1 -> unifyStructAndInterface(struct1, interface2, constraints);
    };
  }

  private static StructTS unifyFieldSetAndStruct(
      FieldSetTS fieldSet, StructTS struct2, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    return switch (fieldSet) {
      case InterfaceTS interface1 -> unifyStructAndInterface(struct2, interface1, constraints);
      case StructTS struct1 -> unifyStructAndStruct(struct1, struct2, constraints);
    };
  }

  private static StructTS unifyStructAndInterface(
      StructTS struct, InterfaceTS interface_, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    var unifiedFields = unifyFieldSetAndFieldSet(struct, interface_, constraints);
    if (unifiedFields.size() != struct.fields().size()) {
      throw new UnifierExc();
    } else {
      return struct;
    }
  }

  private static ImmutableMap<String, ItemSigS> unifyFieldSetAndFieldSet(
      FieldSetTS fieldSet1, FieldSetTS fieldSet2, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    var fields1 = fieldSet1.fieldSet();
    var fields2 = fieldSet2.fieldSet();
    var mergedFields = new HashMap<>(fields1);
    for (Entry<String, ItemSigS> field2 : fields2.entrySet()) {
      var name = field2.getKey();
      var field1 = mergedFields.get(name);
      if (field1 == null) {
        mergedFields.put(name, field2.getValue());
      } else {
        var unifiedType = unifyAndInferConstraints(
            field1.type(), field2.getValue().type(), constraints);
        mergedFields.put(name, new ItemSigS(unifiedType, name));
      }
    }
    return ImmutableMap.copyOf(mergedFields);
  }

  private static StructTS unifyStructAndStruct(
      StructTS struct1, StructTS struct2, Queue<EqualityConstraint> constraints) throws UnifierExc {
    if (!struct1.name().equals(struct2.name())) {
      throw new UnifierExc();
    }
    var itemSigs = zip(
        struct1.fields(),
        struct2.fields(),
        (itemSig1, itemSig2) -> unifyItemSigAndItemSig(itemSig1, itemSig2, constraints));
    return new StructTS(struct1.name(), nlist(itemSigs));
  }

  private static ItemSigS unifyItemSigAndItemSig(
      ItemSigS itemSig1, ItemSigS itemSig2, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    var name1 = itemSig1.name();
    var name2 = itemSig2.name();
    if (!name1.equals(name2)) {
      throw new UnifierExc();
    }
    var type1 = itemSig1.type();
    var type2 = itemSig2.type();
    return new ItemSigS(unifyAndInferConstraints(type1, type2, constraints), name1);
  }

  private static FuncTS unifyFunctionAndType(
      FuncTS func1, TypeS type, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    if (type instanceof FuncTS func2) {
      var result1 = func1.result();
      var result2 = func2.result();
      var result = unifyAndInferConstraints(result1, result2, constraints);
      var params1 = func1.params();
      var params2 = func2.params();
      var params = unifyTupleAndTuple(params1, params2, constraints);
      return new FuncTS(params, result);
    } else {
      throw new UnifierExc();
    }
  }

  private static TupleTS unifyTupleAndType(
      TupleTS tuple1, TypeS type, Queue<EqualityConstraint> constraints)
      throws UnifierExc {
    if (type instanceof TupleTS tuple2) {
      return unifyTupleAndTuple(tuple1, tuple2, constraints);
    } else {
      throw new UnifierExc();
    }
  }

  private static TupleTS unifyTupleAndTuple(
      TupleTS tuple1, TupleTS tuple2, Queue<EqualityConstraint> constraints) throws UnifierExc {
    var elements = zip(
        tuple1.elements(),
        tuple2.elements(),
        (type1, type2) -> unifyAndInferConstraints(type1, type2, constraints));
    return new TupleTS(elements);
  }

  private static <T> ImmutableList<T> zip(
      List<T> items1, List<T> items2, ThrowingBiFunction<T, T, T, UnifierExc> mapper)
      throws UnifierExc {
    if (items1.size() != items2.size()) {
      throw new UnifierExc();
    }
    return Lists.zip(items1, items2, mapper);
  }
}
