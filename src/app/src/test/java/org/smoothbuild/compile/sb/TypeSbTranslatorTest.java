package org.smoothbuild.compile.sb;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableMap;

public class TypeSbTranslatorTest extends TestContext {
  @Nested
  class _mono {
    @Test
    public void blob_type() {
      assertTranslation(blobTS(), blobTB());
    }

    @Test
    public void bool_type() {
      assertTranslation(boolTS(), boolTB());
    }

    @Test
    public void int_type() {
      assertTranslation(intTS(), intTB());
    }

    @Test
    public void string_type() {
      assertTranslation(stringTS(), stringTB());
    }

    @Test
    public void int_array_type() {
      assertTranslation(arrayTS(intTS()), arrayTB(intTB()));
    }

    @Test
    public void tuple_type() {
      assertTranslation(tupleTS(intTS(), blobTS()), tupleTB(intTB(), blobTB()));
    }

    @Test
    public void struct_type() {
      assertTranslation(structTS(intTS(), blobTS()), tupleTB(intTB(), blobTB()));
    }

    @Test
    public void func_type() {
      assertTranslation(funcTS(blobTS(), stringTS(), intTS()), funcTB(blobTB(), stringTB(), intTB()));
    }
  }

  @Nested
  class _poly {
    @Test
    public void array_type() {
      assertTranslation(ImmutableMap.of(varA(), intTB()),
          arrayTS(varA()), arrayTB(intTB()));
    }

    @Test
    public void tuple_type() {
      assertTranslation(ImmutableMap.of(varA(), intTB(), varB(), blobTB()),
          tupleTS(varA(), varB()), tupleTB(intTB(), blobTB()));
    }

    @Test
    public void struct_type() {
      assertTranslation(ImmutableMap.of(varA(), intTB(), varB(), blobTB()),
          structTS(varA(), varB()), tupleTB(intTB(), blobTB()));
    }

    @Test
    public void func_type() {
      assertTranslation(ImmutableMap.of(varA(), intTB(), varB(), blobTB(), varC(), stringTB()),
          funcTS(varB(), varC(), varA()), funcTB(blobTB(), stringTB(), intTB()));
    }

    @Test
    public void missing_mapping_for_variable_causes_exception() {
      assertCall(() -> assertTranslation(arrayTS(varA()), arrayTB(intTB())))
          .throwsException(new IllegalStateException("Unknown variable `A`."));
    }
  }

  private void assertTranslation(TypeS typeS, TypeB expected) {
    assertTranslation(ImmutableMap.of(), typeS, expected);
  }

  private void assertTranslation(ImmutableMap<VarS, TypeB> varMap, TypeS typeS, TypeB expected) {
    var typeSbTranslator = new TypeSbTranslator(bytecodeF(), varMap);
    assertThat(typeSbTranslator.translate(typeS))
        .isEqualTo(expected);
  }
}
