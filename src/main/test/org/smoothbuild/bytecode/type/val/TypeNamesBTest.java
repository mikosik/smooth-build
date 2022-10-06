package org.smoothbuild.bytecode.type.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.arrayTypeName;
import static org.smoothbuild.bytecode.type.val.TypeNamesB.funcTypeName;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

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
      assertThat(funcTypeName(stringTB(), list(blobTB(), boolTB())))
          .isEqualTo("String(Blob,Bool)");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() {
      assertThat(TypeNamesB.tupleTypeName(list(blobTB(), boolTB())))
          .isEqualTo("{Blob,Bool}");
    }
  }
}
