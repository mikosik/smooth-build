package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class EmptySetNodeTest {
  EmptySetNode emptySetNode = new EmptySetNode(codeLocation(1, 2, 4));

  @Test
  public void type() {
    assertThat(emptySetNode.type()).isEqualTo(Type.EMPTY_SET);
  }

  public void generateTaskThrowsException() throws Exception {
    Task task = emptySetNode.generateTask();
    task.execute(new FakeSandbox());
    assertThat((FileSet) task.result()).isEmpty();
  }
}
