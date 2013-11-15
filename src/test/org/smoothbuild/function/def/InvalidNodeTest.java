package org.smoothbuild.function.def;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Type.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class InvalidNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  InvalidNode invalidNode = new InvalidNode(STRING, codeLocation);

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() throws Exception {
    new InvalidNode(null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_code_location_is_forbidden() throws Exception {
    new InvalidNode(STRING, null);
  }

  @Test
  public void type() {
    given(invalidNode = new InvalidNode(STRING, codeLocation));
    when(invalidNode.type());
    thenReturned(STRING);
  }

  @Test
  public void code_location() throws Exception {
    given(invalidNode = new InvalidNode(STRING, codeLocation));
    when(invalidNode.codeLocation());
    thenReturned(codeLocation);
  }

  public void generate_task_throws_exception() throws Exception {
    when(invalidNode).generateTask(taskGenerator);
    thenThrown(RuntimeException.class);
  }
}
