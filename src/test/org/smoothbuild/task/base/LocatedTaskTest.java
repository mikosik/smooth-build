package org.smoothbuild.task.base;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class LocatedTaskTest {
  String name = "name";
  Value value = mock(Value.class);
  Sandbox sandbox = mock(Sandbox.class);

  Task task = mock(Task.class);
  CodeLocation codeLocation = new FakeCodeLocation();

  LocatedTask locatedTask;

  @Test
  public void name_returns_value_from_wrapped_task_name() throws Exception {
    BDDMockito.given(task.name()).willReturn(name);
    given(locatedTask = new LocatedTask(task, codeLocation));
    when(locatedTask.name());
    thenReturned(name);
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
