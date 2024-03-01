package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.severity;
import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class ContainerTest extends TestVirtualMachine {
  @Test
  public void file_system() {
    assertThat(container().fileSystem()).isSameInstanceAs(projectFileSystem());
  }

  @Test
  public void messages_are_logged() throws Exception {
    var container = container();
    container.log().error("message");
    Iterable<TupleB> iterable = container.messages().elements(TupleB.class);
    assertThat(iterable).hasSize(1);
    TupleB tuple = iterable.iterator().next();
    assertThat(text(tuple)).isEqualTo("message");
    assertThat(severity(tuple)).isEqualTo("ERROR");
  }
}
