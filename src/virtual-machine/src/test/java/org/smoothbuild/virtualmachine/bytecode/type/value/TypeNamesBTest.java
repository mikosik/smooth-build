package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.bytecode.type.value.TypeNamesB.arrayTypeName;
import static org.smoothbuild.virtualmachine.bytecode.type.value.TypeNamesB.funcTypeName;
import static org.smoothbuild.virtualmachine.bytecode.type.value.TypeNamesB.tupleTypeName;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class TypeNamesBTest extends TestVirtualMachine {
  @Nested
  class _array_type_name {
    @Test
    public void array_type_name() throws Exception {
      assertThat(arrayTypeName(stringTB())).isEqualTo("[String]");
    }
  }

  @Nested
  class _func_type_name {
    @Test
    public void func_type_name() throws Exception {
      assertThat(funcTypeName(list(blobTB(), boolTB()), stringTB()))
          .isEqualTo("(Blob,Bool)->String");
    }
  }

  @Nested
  class _tuple_type_name {
    @Test
    public void func_type_name() throws Exception {
      assertThat(tupleTypeName(list(blobTB(), boolTB()))).isEqualTo("{Blob,Bool}");
    }
  }
}