package org.smoothbuild.task.base;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.testory.common.Closure;

public class LocatedTaskTest {
  String name = "name";
  Value value = mock(Value.class);
  SandboxImpl sandbox = mock(SandboxImpl.class);

  Task task = mock(Task.class);
  CodeLocation codeLocation = new FakeCodeLocation();

  LocatedTask locatedTask;

  @Test
  public void null_task_is_forbidden() throws Exception {
    when($locatedTask(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() throws Exception {
    when($locatedTask(task, null));
    thenThrown(NullPointerException.class);
  }

  private static Closure $locatedTask(final Task task, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new LocatedTask(task, codeLocation);
      }
    };
  }

  @Test
  public void name_returns_value_from_wrapped_task_name() throws Exception {
    BDDMockito.given(task.name()).willReturn(name);
    given(locatedTask = new LocatedTask(task, codeLocation));
    when(locatedTask.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_forwards_negative_result_from_wrapped_task() throws Exception {
    BDDMockito.given(task.isInternal()).willReturn(false);
    given(locatedTask = new LocatedTask(task, codeLocation));
    when(locatedTask.isInternal());
    thenReturned(false);
  }

  @Test
  public void is_internal_forwards_positive_result_from_wrapped_task() throws Exception {
    BDDMockito.given(task.isInternal()).willReturn(true);
    given(locatedTask = new LocatedTask(task, codeLocation));
    when(locatedTask.isInternal());
    thenReturned(true);
  }

  @Test
  public void execute_returns_value_from_wrapped_task_execute() throws Exception {
    BDDMockito.given(task.execute(sandbox)).willReturn(value);
    given(locatedTask = new LocatedTask(task, codeLocation));
    when(locatedTask.execute(sandbox));
    thenReturned(value);
  }

  @Test
  public void code_location_returns_value_passed_to_constructor() {
    given(locatedTask = new LocatedTask(task, codeLocation));
    when(locatedTask.codeLocation());
    thenReturned(codeLocation);
  }
}
