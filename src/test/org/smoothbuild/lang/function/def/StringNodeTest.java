package org.smoothbuild.lang.function.def;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.testory.common.Closure;

public class StringNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  SString string = mock(SString.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  StringNode stringNode = new StringNode(string, codeLocation);
  Task task;

  @Test
  public void null_string_value_is_forbidden() throws Exception {
    when($stringNode(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() throws Exception {
    when($stringNode(string, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    when(stringNode.type());
    thenReturned(STRING);
  }

  @Test
  public void generateTask() throws Exception {
    given(task = stringNode.generateTask(taskGenerator));
    when(task.execute(new FakeSandbox()));
    thenReturned(string);
  }

  private static Closure $stringNode(final SString string, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new StringNode(string, codeLocation);
      }
    };
  }
}
