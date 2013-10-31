package org.smoothbuild.function.def;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.testory.common.Closure;

public class LocatedNodeImplTest {
  Sandbox sandbox = mock(Sandbox.class);
  Value value = mock(Value.class);
  Task task = mock(Task.class);
  LocatedTask locatedTask;
  Node node = mock(Node.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  TaskGenerator taskGenerator = mock(TaskGenerator.class);

  LocatedNodeImpl locatedNodeImpl;

  @Test
  public void null_node_is_forbidden() {
    when($locatedNodeImpl(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($locatedNodeImpl(node, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_returns_type_of_wrapped_node() throws Exception {
    Type type = Type.STRING;
    BDDMockito.given(node.type()).willReturn(type);

    given(locatedNodeImpl = new LocatedNodeImpl(node, codeLocation));
    when(locatedNodeImpl.type());
    thenReturned(type);
  }

  @Test
  public void generate_task_returns_located_task_with_code_location_that_was_passed_to_constructor()
      throws Exception {
    BDDMockito.given(node.generateTask(taskGenerator)).willReturn(task);

    given(locatedNodeImpl = new LocatedNodeImpl(node, codeLocation));
    given(locatedTask = locatedNodeImpl.generateTask(taskGenerator));
    when(locatedTask.codeLocation());
    thenReturned(codeLocation);
  }

  @Test
  public void generate_task_returns_located_task_which_forwards_execute_to_task_generated_by_wrapped_node()
      throws Exception {
    BDDMockito.given(node.generateTask(taskGenerator)).willReturn(task);
    BDDMockito.given(task.execute(sandbox)).willReturn(value);

    given(locatedNodeImpl = new LocatedNodeImpl(node, codeLocation));
    given(locatedTask = locatedNodeImpl.generateTask(taskGenerator));
    when(locatedTask.execute(sandbox));
    thenReturned(value);
  }

  private static Closure $locatedNodeImpl(final Node node, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new LocatedNodeImpl(node, codeLocation);
      }
    };
  }
}
