package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.levelAsString;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class ContainerTest extends VmTestContext {
  @Test
  void messages_are_logged() throws Exception {
    var container = provide().container();
    container.log().error("message");
    Iterable<BTuple> iterable = container.messages().elements(BTuple.class);
    assertThat(iterable).hasSize(1);
    BTuple tuple = iterable.iterator().next();
    assertThat(message(tuple)).isEqualTo("message");
    assertThat(levelAsString(tuple)).isEqualTo("ERROR");
  }
}
