package org.smoothbuild.compilerfrontend.lang.type.tool;

import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Queue;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.function.Function2;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SInterfaceType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public class ConstraintInferrer {
  public static SType unifyAndInferConstraints(
      SType type1, SType type2, Queue<Constraint> constraints) throws UnifierException {
    if (type1.isFlexibleVar()) {
      constraints.add(new Constraint(type1, type2));
      return type1;
    }
    if (type2.isFlexibleVar()) {
      constraints.add(new Constraint(type1, type2));
      return type2;
    }
    return switch (type1) {
      case SArrayType array1 -> unifyArray(array1, type2, constraints);
      case SInterfaceType fieldSet1 -> unifyInterface(fieldSet1, type2, constraints);
      case SFuncType func1 -> unifyFunction(func1, type2, constraints);
      case STupleType tuple1 -> unifyTuple(tuple1, type2, constraints);
      case SVar sVar -> assertTypesAreEqual(sVar, type2);
      default -> assertTypesAreEqual(type1, type2);
    };
  }

  private static SType assertTypesAreEqual(SType type1, SType type2) throws UnifierException {
    if (type1.equals(type2)) {
      return type1;
    } else {
      throw new UnifierException();
    }
  }

  private static SArrayType unifyArray(
      SArrayType array1, SType type2, Queue<Constraint> constraints) throws UnifierException {
    if (type2 instanceof SArrayType array2) {
      return new SArrayType(unifyAndInferConstraints(array1.elem(), array2.elem(), constraints));
    } else {
      throw new UnifierException();
    }
  }

  private static SInterfaceType unifyInterface(
      SInterfaceType type1, SType type2, Queue<Constraint> c) throws UnifierException {
    return switch (type2) {
      case SStructType struct2 -> switch (type1) {
        case SStructType struct1 -> unifyStructAndStruct(struct1, struct2, c);
        case SInterfaceType interface1 -> unifyStructAndInterface(struct2, interface1, c);
      };
      case SInterfaceType interface2 -> switch (type1) {
        case SStructType struct1 -> unifyStructAndInterface(struct1, interface2, c);
        case SInterfaceType interface1 -> unifyInterfaceAndInterface(interface1, interface2, c);
      };
      default -> throw new UnifierException();
    };
  }

  private static SInterfaceType unifyInterfaceAndInterface(
      SInterfaceType interface1, SInterfaceType interface2, Queue<Constraint> constraints)
      throws UnifierException {
    return new SInterfaceType(unifyInterfaceFields(interface1, interface2, constraints));
  }

  private static SStructType unifyStructAndInterface(
      SStructType struct, SInterfaceType interface_, Queue<Constraint> constraints)
      throws UnifierException {
    var unifiedFields = unifyInterfaceFields(struct, interface_, constraints);
    if (unifiedFields.size() != struct.fields().size()) {
      throw new UnifierException();
    } else {
      return struct;
    }
  }

  private static Map<Name, SItemSig> unifyInterfaceFields(
      SInterfaceType interface1, SInterfaceType interface2, Queue<Constraint> constraints)
      throws UnifierException {
    var fields1 = interface1.fieldSet();
    var fields2 = interface2.fieldSet();
    var mergedFields = new HashMap<>(fields1.asJdkMap());
    for (Entry<Name, SItemSig> field2 : fields2.entrySet()) {
      var name = field2.getKey();
      var field1 = mergedFields.get(name);
      if (field1 == null) {
        mergedFields.put(name, field2.getValue());
      } else {
        var unifiedType =
            unifyAndInferConstraints(field1.type(), field2.getValue().type(), constraints);
        mergedFields.put(name, new SItemSig(unifiedType, name));
      }
    }
    return mapOfAll(mergedFields);
  }

  private static SStructType unifyStructAndStruct(
      SStructType struct1, SStructType struct2, Queue<Constraint> constraints)
      throws UnifierException {
    if (!struct1.fqn().equals(struct2.fqn())) {
      throw new UnifierException();
    }
    var itemSigs = zip(
        struct1.fields().list(),
        struct2.fields().list(),
        (itemSig1, itemSig2) -> unifyItemSigAndItemSig(itemSig1, itemSig2, constraints));
    return new SStructType(struct1.fqn(), nlist(itemSigs));
  }

  private static SItemSig unifyItemSigAndItemSig(
      SItemSig itemSig1, SItemSig itemSig2, Queue<Constraint> constraints) throws UnifierException {
    var name1 = itemSig1.name();
    var name2 = itemSig2.name();
    if (!name1.equals(name2)) {
      throw new UnifierException();
    }
    var type1 = itemSig1.type();
    var type2 = itemSig2.type();
    return new SItemSig(unifyAndInferConstraints(type1, type2, constraints), name1);
  }

  private static SFuncType unifyFunction(SFuncType func1, SType type, Queue<Constraint> constraints)
      throws UnifierException {
    if (type instanceof SFuncType func2) {
      var result1 = func1.result();
      var result2 = func2.result();
      var result = unifyAndInferConstraints(result1, result2, constraints);
      var params1 = func1.params();
      var params2 = func2.params();
      var params = unifyTupleAndTuple(params1, params2, constraints);
      return new SFuncType(params, result);
    } else {
      throw new UnifierException();
    }
  }

  private static STupleType unifyTuple(STupleType tuple1, SType type, Queue<Constraint> constraints)
      throws UnifierException {
    if (type instanceof STupleType tuple2) {
      return unifyTupleAndTuple(tuple1, tuple2, constraints);
    } else {
      throw new UnifierException();
    }
  }

  private static STupleType unifyTupleAndTuple(
      STupleType tuple1, STupleType tuple2, Queue<Constraint> constraints) throws UnifierException {
    var elements = zip(
        tuple1.elements(),
        tuple2.elements(),
        (type1, type2) -> unifyAndInferConstraints(type1, type2, constraints));
    return new STupleType(elements);
  }

  private static <T> List<T> zip(
      List<T> items1, List<T> items2, Function2<T, T, T, UnifierException> mapper)
      throws UnifierException {
    if (items1.size() != items2.size()) {
      throw new UnifierException();
    }
    return items1.zip(items2, mapper);
  }
}
