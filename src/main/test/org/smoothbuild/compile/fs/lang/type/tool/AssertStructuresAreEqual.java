package org.smoothbuild.compile.fs.lang.type.tool;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.util.collect.Lists.allMatch;

import java.util.Map;
import java.util.Objects;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.InterfaceTS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TempVarS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

public class AssertStructuresAreEqual {
  public static void assertStructuresAreEqual(TypeS actual, TypeS expected) {
    assertWithMessage("Structure of " + actual.q() + " and " + expected.q() + " should be equal.")
        .that(structuresAreEqual(actual, expected))
        .isTrue();
  }

  private static boolean structuresAreEqual(TypeS actual, TypeS expected) {
    return switch (actual) {
      case ArrayTS a -> arrayStructuresAreEqual(a, expected);
      case FuncTS f -> funcStructuresAreEqual(f, expected);
      case TupleTS tupleTS -> tupleStructuresAreEqual(tupleTS, expected);
      case InterfaceTS interfaceTS -> interfaceStructuresAreEqual(interfaceTS, expected);
      case StructTS structTS -> structStructuresAreEqual(structTS, expected);
      case TempVarS tempVarS -> expected instanceof TempVarS;
      default -> actual.equals(expected);
    };
  }

  private static boolean arrayStructuresAreEqual(ArrayTS arrayTS, TypeS expected) {
    return expected instanceof ArrayTS otherArrayTS
        && structuresAreEqual(arrayTS.elem(), otherArrayTS.elem());
  }

  private static boolean funcStructuresAreEqual(FuncTS funcTS, TypeS expected) {
    return expected instanceof FuncTS otherFuncTS
        && structuresAreEqual(funcTS.result(), otherFuncTS.result())
        && structuresAreEqual(funcTS.params(), otherFuncTS.params());
  }

  private static boolean tupleStructuresAreEqual(TupleTS tupleTS, TypeS expected) {
    return expected instanceof TupleTS otherTupleTS
        && tupleElementStructuresAreEqual(tupleTS, otherTupleTS);
  }

  private static boolean tupleElementStructuresAreEqual(TupleTS tupleTS1, TupleTS tupleTS2) {
    return allMatch(
        tupleTS1.elements(), tupleTS2.elements(), AssertStructuresAreEqual::structuresAreEqual);
  }

  private static boolean interfaceStructuresAreEqual(InterfaceTS interfaceTS, TypeS expected) {
    return expected instanceof InterfaceTS otherInterfaceTS
        && fieldSetStructuresAreEqual(interfaceTS.fieldSet(), otherInterfaceTS.fieldSet());
  }

  private static boolean structStructuresAreEqual(StructTS structTS, TypeS expected) {
    return expected instanceof StructTS otherStructTS
        && Objects.equals(structTS.name(), otherStructTS.name())
        && fieldSetStructuresAreEqual(structTS.fieldSet(), otherStructTS.fieldSet());
  }

  private static boolean fieldSetStructuresAreEqual(
      Map<String, ItemSigS> fieldSet1, Map<String, ItemSigS> fieldSet2) {
    return Objects.equals(fieldSet1.keySet(), fieldSet2.keySet())
        && fieldTypeStructuresAreEqual(fieldSet1, fieldSet2);
  }

  private static boolean fieldTypeStructuresAreEqual(
      Map<String, ItemSigS> fieldSet1, Map<String, ItemSigS> fieldSet2) {
    for (String name : fieldSet1.keySet()) {
      var item1 = fieldSet1.get(name);
      var item2 = fieldSet2.get(name);
      if (!structuresAreEqual(item1.type(), item2.type())) {
        return false;
      }
    }
    return true;
  }
}
