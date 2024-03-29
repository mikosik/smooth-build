package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.arrayTypeName;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.lambdaTypeName;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.BTypeNames.tupleTypeName;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BTypeNamesTest extends TestingVirtualMachine {
  @Test
  public void array_type_name() throws Exception {
    assertThat(arrayTypeName(bStringType())).isEqualTo("[String]");
  }

  @Test
  public void lambda_type_name() throws Exception {
    assertThat(lambdaTypeName(list(bBlobType(), bBoolType()), bStringType()))
        .isEqualTo("(Blob,Bool)->String");
  }

  @Test
  public void tuple_type_name() throws Exception {
    assertThat(tupleTypeName(list(bBlobType(), bBoolType()))).isEqualTo("{Blob,Bool}");
  }
}
