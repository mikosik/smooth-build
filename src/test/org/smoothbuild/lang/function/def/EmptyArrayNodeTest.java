package org.smoothbuild.lang.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class EmptyArrayNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  EmptyArrayNode emptyArrayNode = new EmptyArrayNode(codeLocation);

  @Test
  public void type() {
    when(emptyArrayNode.type());
    thenReturned(EMPTY_ARRAY);
  }

  @Test
  public void code_location() throws Exception {
    given(emptyArrayNode = new EmptyArrayNode(codeLocation));
    when(emptyArrayNode.codeLocation());
    thenReturned(codeLocation);
  }

  @SuppressWarnings("unchecked")
  public void generate_task() throws Exception {
    Task task = emptyArrayNode.generateTask(taskGenerator);
    SValue result = task.execute(new FakeSandbox());
    assertThat((SArray<SFile>) result).isEmpty();
  }
}
