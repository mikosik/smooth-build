package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.levelAsString;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ContainerTest extends TestingVirtualMachine {
  @Test
  public void file_system() {
    assertThat(container().bucket()).isSameInstanceAs(projectBucket());
  }

  @Test
  public void messages_are_logged() throws Exception {
    var container = container();
    container.log().error("message");
    Iterable<TupleB> iterable = container.messages().elements(TupleB.class);
    assertThat(iterable).hasSize(1);
    TupleB tuple = iterable.iterator().next();
    assertThat(message(tuple)).isEqualTo("message");
    assertThat(levelAsString(tuple)).isEqualTo("ERROR");
  }
}
