package org.smoothbuild.compilerbackend;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sArrayType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBoolType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varB;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varC;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TypeSbTranslatorTest extends TestingVirtualMachine {
  @Nested
  class _mono {
    @Test
    public void blob_type() throws Exception {
      assertTranslation(sBlobType(), bBlobType());
    }

    @Test
    public void bool_type() throws Exception {
      assertTranslation(sBoolType(), bBoolType());
    }

    @Test
    public void int_type() throws Exception {
      assertTranslation(sIntType(), bIntType());
    }

    @Test
    public void string_type() throws Exception {
      assertTranslation(sStringType(), bStringType());
    }

    @Test
    public void int_array_type() throws Exception {
      assertTranslation(sArrayType(sIntType()), bArrayType(bIntType()));
    }

    @Test
    public void tuple_type() throws Exception {
      assertTranslation(
          TestingSExpression.sTupleType(sIntType(), sBlobType()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void struct_type() throws Exception {
      assertTranslation(
          TestingSExpression.sStructType(sIntType(), sBlobType()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void func_type() throws Exception {
      assertTranslation(
          TestingSExpression.sFuncType(sBlobType(), sStringType(), sIntType()),
          bFuncType(bBlobType(), bStringType(), bIntType()));
    }
  }

  @Nested
  class _poly {
    @Test
    public void array_type() throws Exception {
      assertTranslation(map(varA(), bIntType()), sArrayType(varA()), bArrayType(bIntType()));
    }

    @Test
    public void tuple_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType()),
          TestingSExpression.sTupleType(varA(), varB()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void struct_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType()),
          TestingSExpression.sStructType(varA(), varB()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    public void func_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType(), varC(), bStringType()),
          TestingSExpression.sFuncType(varB(), varC(), varA()),
          bFuncType(bBlobType(), bStringType(), bIntType()));
    }

    @Test
    public void missing_mapping_for_variable_causes_exception() {
      assertCall(() -> assertTranslation(sArrayType(varA()), bArrayType(bIntType())))
          .throwsException(new IllegalStateException("Unknown variable `A`."));
    }
  }

  private void assertTranslation(SType sType, BType expected) throws SbTranslatorException {
    assertTranslation(map(), sType, expected);
  }

  private void assertTranslation(Map<SVar, BType> varMap, SType sType, BType expected)
      throws SbTranslatorException {
    var typeSbTranslator = new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeF()), varMap);
    assertThat(typeSbTranslator.translate(sType)).isEqualTo(expected);
  }
}
