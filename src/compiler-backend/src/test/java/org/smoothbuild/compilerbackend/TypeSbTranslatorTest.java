package org.smoothbuild.compilerbackend;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.arrayTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.blobTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.boolTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.funcTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.structTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.tupleTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.varA;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.varB;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.varC;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.lang.type.VarS;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TypeSbTranslatorTest extends TestingVirtualMachine {
  @Nested
  class _mono {
    @Test
    public void blob_type() throws Exception {
      assertTranslation(blobTS(), blobTB());
    }

    @Test
    public void bool_type() throws Exception {
      assertTranslation(boolTS(), boolTB());
    }

    @Test
    public void int_type() throws Exception {
      assertTranslation(intTS(), intTB());
    }

    @Test
    public void string_type() throws Exception {
      assertTranslation(stringTS(), stringTB());
    }

    @Test
    public void int_array_type() throws Exception {
      assertTranslation(arrayTS(intTS()), arrayTB(intTB()));
    }

    @Test
    public void tuple_type() throws Exception {
      assertTranslation(tupleTS(intTS(), blobTS()), tupleTB(intTB(), blobTB()));
    }

    @Test
    public void struct_type() throws Exception {
      assertTranslation(structTS(intTS(), blobTS()), tupleTB(intTB(), blobTB()));
    }

    @Test
    public void func_type() throws Exception {
      assertTranslation(
          funcTS(blobTS(), stringTS(), intTS()), funcTB(blobTB(), stringTB(), intTB()));
    }
  }

  @Nested
  class _poly {
    @Test
    public void array_type() throws Exception {
      assertTranslation(map(varA(), intTB()), arrayTS(varA()), arrayTB(intTB()));
    }

    @Test
    public void tuple_type() throws Exception {
      assertTranslation(
          map(varA(), intTB(), varB(), blobTB()),
          tupleTS(varA(), varB()),
          tupleTB(intTB(), blobTB()));
    }

    @Test
    public void struct_type() throws Exception {
      assertTranslation(
          map(varA(), intTB(), varB(), blobTB()),
          structTS(varA(), varB()),
          tupleTB(intTB(), blobTB()));
    }

    @Test
    public void func_type() throws Exception {
      assertTranslation(
          map(varA(), intTB(), varB(), blobTB(), varC(), stringTB()),
          funcTS(varB(), varC(), varA()),
          funcTB(blobTB(), stringTB(), intTB()));
    }

    @Test
    public void missing_mapping_for_variable_causes_exception() {
      assertCall(() -> assertTranslation(arrayTS(varA()), arrayTB(intTB())))
          .throwsException(new IllegalStateException("Unknown variable `A`."));
    }
  }

  private void assertTranslation(TypeS typeS, TypeB expected) throws SbTranslatorException {
    assertTranslation(map(), typeS, expected);
  }

  private void assertTranslation(Map<VarS, TypeB> varMap, TypeS typeS, TypeB expected)
      throws SbTranslatorException {
    var typeSbTranslator = new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeF()), varMap);
    assertThat(typeSbTranslator.translate(typeS)).isEqualTo(expected);
  }
}
