package org.smoothbuild.compilerbackend;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class TypeSbTranslatorTest extends FrontendCompilerTestContext {
  @Nested
  class _mono {
    @Test
    void blob_type() throws Exception {
      assertTranslation(sBlobType(), bBlobType());
    }

    @Test
    void bool_type() throws Exception {
      assertTranslation(sBoolType(), bBoolType());
    }

    @Test
    void int_type() throws Exception {
      assertTranslation(sIntType(), bIntType());
    }

    @Test
    void string_type() throws Exception {
      assertTranslation(sStringType(), bStringType());
    }

    @Test
    void int_array_type() throws Exception {
      assertTranslation(sIntArrayT(), bIntArrayType());
    }

    @Test
    void tuple_type() throws Exception {
      assertTranslation(sTupleType(sIntType(), sBlobType()), bTupleType(bIntType(), bBlobType()));
    }

    @Test
    void struct_type() throws Exception {
      assertTranslation(sStructType(sIntType(), sBlobType()), bTupleType(bIntType(), bBlobType()));
    }

    @Test
    void func_type() throws Exception {
      assertTranslation(
          sFuncType(sBlobType(), sStringType(), sIntType()),
          bLambdaType(bBlobType(), bStringType(), bIntType()));
    }
  }

  @Nested
  class _poly {
    @Test
    void array_type() throws Exception {
      assertTranslation(map(varA(), bIntType()), sVarAArrayT(), bIntArrayType());
    }

    @Test
    void tuple_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType()),
          sTupleType(varA(), varB()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    void struct_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType()),
          sStructType(varA(), varB()),
          bTupleType(bIntType(), bBlobType()));
    }

    @Test
    void func_type() throws Exception {
      assertTranslation(
          map(varA(), bIntType(), varB(), bBlobType(), varC(), bStringType()),
          sFuncType(varB(), varC(), varA()),
          bLambdaType(bBlobType(), bStringType(), bIntType()));
    }

    @Test
    void missing_mapping_for_variable_causes_exception() {
      assertCall(() -> assertTranslation(sVarAArrayT(), bIntArrayType()))
          .throwsException(new IllegalStateException("Unknown variable `A`."));
    }
  }

  private void assertTranslation(SType sType, BType expected) throws SbTranslatorException {
    assertTranslation(map(), sType, expected);
  }

  private void assertTranslation(Map<STypeVar, BType> typeVarMap, SType sType, BType expected)
      throws SbTranslatorException {
    var typeSbTranslator =
        new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeF()), typeVarMap);
    assertThat(typeSbTranslator.translate(sType)).isEqualTo(expected);
  }
}
