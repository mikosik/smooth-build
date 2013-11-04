package org.smoothbuild.task.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;

public class InternalTaskTest {
  InternalTask internalTask;

  @Test
  public void test() {
    given(internalTask = new MyInternalTask());
    when(internalTask.isInternal());
    thenReturned(true);
  }

  private static class MyInternalTask extends InternalTask {
    @Override
    public String name() {
      return null;
    }

    @Override
    public Value execute(SandboxImpl sandbox) {
      return null;
    }
  }
}
