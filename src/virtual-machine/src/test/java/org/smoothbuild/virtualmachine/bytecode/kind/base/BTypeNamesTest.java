package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.arrayTypeName;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.choiceTypeName;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.lambdaTypeName;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.tupleTypeName;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BTypeNamesTest extends VmTestContext {
  @Test
  void array_type_name() throws Exception {
    assertThat(arrayTypeName(bStringType())).isEqualTo("[String]");
  }

  @Test
  void lambda_type_name() throws Exception {
    assertThat(lambdaTypeName(list(bBlobType(), bBoolType()), bStringType()))
        .isEqualTo("(Blob,Bool)->String");
  }

  @Test
  void tuple_type_name() throws Exception {
    assertThat(tupleTypeName(list(bBlobType(), bBoolType()))).isEqualTo("{Blob,Bool}");
  }

  @Test
  void choice_type_name_with_single_alternative() throws Exception {
    assertThat(choiceTypeName(list(bIntType()))).isEqualTo("{Int|}");
  }

  @Test
  void choice_type_name_with_multiple_alternatives() throws Exception {
    assertThat(choiceTypeName(list(bBlobType(), bIntType()))).isEqualTo("{Blob|Int}");
  }
}
