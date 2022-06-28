package org.smoothbuild.bytecode.type.cnst;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.arrayTypeName;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.funcTypeName;
import static org.smoothbuild.testing.type.TestingTB.BLOB;
import static org.smoothbuild.testing.type.TestingTB.BOOL;
import static org.smoothbuild.testing.type.TestingTB.STRING;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TNamesBTest {
  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() {
      assertThat(arrayTypeName(STRING))
          .isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() {
      assertThat(funcTypeName(STRING, list(BLOB, BOOL)))
          .isEqualTo("String(Blob, Bool)");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() {
      assertThat(TNamesB.tupleTypeName(list(BLOB, BOOL)))
          .isEqualTo("{Blob, Bool}");
    }
  }
}
