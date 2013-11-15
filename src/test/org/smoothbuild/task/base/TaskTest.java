package org.smoothbuild.task.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.plugin.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.testory.common.Closure;

public class TaskTest {
  String name = "name";
  FakeCodeLocation codeLocation = new FakeCodeLocation();

  Task task;

  @Test
  public void null_name_is_forbidden() {
    when($myTask(null, true, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($myTask(name, true, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void name() throws Exception {
    given(task = new MyTask(name, false, codeLocation));
    when(task.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_return_true_when_true_passed_to_constructor() throws Exception {
    given(task = new MyTask(name, true, codeLocation));
    when(task.isInternal());
    thenReturned(true);
  }

  @Test
  public void is_internal_return_false_when_false_passed_to_constructor() throws Exception {
    given(task = new MyTask(name, false, codeLocation));
    when(task.isInternal());
    thenReturned(false);
  }

  @Test
  public void code_location() throws Exception {
    given(task = new MyTask(name, false, codeLocation));
    when(task.codeLocation());
    thenReturned(codeLocation);
  }

  private static Closure $myTask(final String name, final boolean isInternal,
      final FakeCodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyTask(name, isInternal, codeLocation);
      }
    };
  }

  public static class MyTask extends Task {
    public MyTask(String name, boolean isInternal, CodeLocation codeLocation) {
      super(name, isInternal, codeLocation);
    }

    @Override
    public Value execute(SandboxImpl sandbox) {
      return null;
    }
  }
}
