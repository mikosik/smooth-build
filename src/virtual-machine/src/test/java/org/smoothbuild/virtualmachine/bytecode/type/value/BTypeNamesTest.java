package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.bytecode.type.value.BTypeNames.arrayTypeName;
import static org.smoothbuild.virtualmachine.bytecode.type.value.BTypeNames.funcTypeName;
import static org.smoothbuild.virtualmachine.bytecode.type.value.BTypeNames.tupleTypeName;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BTypeNamesTest extends TestingVirtualMachine {
  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() throws Exception {
      assertThat(arrayTypeName(bStringType())).isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() throws Exception {
      assertThat(funcTypeName(list(bBlobType(), bBoolType()), bStringType()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() throws Exception {
      assertThat(tupleTypeName(list(bBlobType(), bBoolType()))).isEqualTo("{Blob,Bool}");
    }
  }
}