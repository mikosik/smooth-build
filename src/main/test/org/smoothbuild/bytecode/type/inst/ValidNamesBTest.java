package org.smoothbuild.bytecode.type.inst;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.bytecode.type.inst.ValidNamesB.arrayTypeName;
import static org.smoothbuild.bytecode.type.inst.ValidNamesB.funcTypeName;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class ValidNamesBTest extends TestContext {
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
      assertThat(funcTypeName(list(blobTB(), boolTB()), stringTB()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() {
      assertThat(ValidNamesB.tupleTypeName(list(blobTB(), boolTB())))
          .isEqualTo("{Blob,Bool}");
    }
  }
}
