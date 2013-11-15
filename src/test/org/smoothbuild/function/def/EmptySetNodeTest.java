package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class EmptySetNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  EmptySetNode emptySetNode = new EmptySetNode(codeLocation);

  @Test
  public void type() {
    when(emptySetNode.type());
    thenReturned(EMPTY_SET);
  }

  @Test
  public void code_location() throws Exception {
    given(emptySetNode = new EmptySetNode(codeLocation));
    when(emptySetNode.codeLocation());
    thenReturned(codeLocation);
  }

  public void generate_task() throws Exception {
    Task task = emptySetNode.generateTask(taskGenerator);
    Value result = task.execute(new FakeSandbox());
    assertThat((FileSet) result).isEmpty();
  }
}
