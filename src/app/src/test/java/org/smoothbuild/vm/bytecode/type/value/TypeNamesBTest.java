package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.vm.bytecode.type.value.TypeNamesB.arrayTypeName;
import static org.smoothbuild.vm.bytecode.type.value.TypeNamesB.funcTypeName;
import static org.smoothbuild.vm.bytecode.type.value.TypeNamesB.tupleTypeName;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import io.vavr.collection.Array;

public class TypeNamesBTest extends TestContext {
  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(arrayTypeName(stringTB()))
          .isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(funcTypeName(Array.of(blobTB(), boolTB()), stringTB()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() {
      assertThat(tupleTypeName(Array.of(blobTB(), boolTB())))
          .isEqualTo("{Blob,Bool}");
    }
  }
}
