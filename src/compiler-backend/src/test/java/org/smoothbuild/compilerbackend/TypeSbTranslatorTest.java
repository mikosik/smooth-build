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
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TypeSbTranslatorTest extends TestingVirtualMachine {
  @Nested
  class _mono {
    @Test
    public void blob_type() throws Exception {
      assertTranslation(blobTS(), bBlobType());
    }

    @Test
    public void bool_type() throws Exception {
      assertTranslation(boolTS(), bBoolType());
    }

    @Test
    public void int_type() throws Exception {
      assertTranslation(intTS(), bIntType());
    }

    @Test
    public void string_type() throws Exception {
      assertTranslation(stringTS(), bStringType());
    }

    @Test
    public void int_array_type() throws Exception {
      assertTranslation(arrayTS(intTS()), bArrayType(bIntType()));
    }

    @Test
    public void tuple_type() throws Exception {
      assertTranslation(tupleTS(intTS(), blobTS()), bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void struct_type() throws Exception {
      assertTranslation(structTS(intTS(), blobTS()), bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void func_type() throws Exception {
      assertTranslation(
          funcTS(blobTS(), stringTS(), intTS()), bFuncType(bBlobType(), bStringType(), bIntType()));
    }
  }

  @Nested
  class _poly {
    @Test
    public void array_type() throws Exception {
      assertTranslation(map(varA(), bIntType()), arrayTS(varA()), bArrayType(bIntType()));
    }

    @Test
    public void tuple_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType()),
          tupleTS(varA(), varB()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void struct_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType()),
          structTS(varA(), varB()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void func_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType(), varC(), bStringType()),
          funcTS(varB(), varC(), varA()),
          bFuncType(bBlobType(), bStringType(), bIntType()));
    }

    @Test
    public void missing_mapping_for_variable_causes_exception() {
      assertCall(() -> assertTranslation(arrayTS(varA()), bArrayType(bIntType())))
          .throwsException(new IllegalStateException("Unknown variable `A`."));
    }
  }

  private void assertTranslation(TypeS typeS, BType expected) throws SbTranslatorException {
    assertTranslation(map(), typeS, expected);
  }

  private void assertTranslation(Map<VarS, BType> varMap, TypeS typeS, BType expected)
      throws SbTranslatorException {
    var typeSbTranslator = new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeF()), varMap);
    assertThat(typeSbTranslator.translate(typeS)).isEqualTo(expected);
  }
}
