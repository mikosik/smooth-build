package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class AbstractTaskTest {

  @Test
  public void resultNotSetInitiallyWhenConstructorWithoutResultParamUsed() {
    MyAbstractTask task = new MyAbstractTask(Empty.stringTaskMap());
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void resultSetInitiallyWhenConstructorWithResultParamUsed() {
    String result = "result";
    MyAbstractTask task = new MyAbstractTask(result, Empty.stringTaskMap());

    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }

  @Test
  public void resultIsSetAfterSetting() throws Exception {
    doTestResultSetting("result");
  }

  @Test
  public void resultIsSetAfterSettingNull() throws Exception {
    doTestResultSetting(null);
  }

  private void doTestResultSetting(Object result) {
    MyAbstractTask task = new MyAbstractTask(Empty.stringTaskMap());

    task.setMyResult(result);

    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }

  @Test
  public void dependencies() throws Exception {
    ImmutableMap<String, Task> dependencies = ImmutableMap.of("key", mock(Task.class));
    MyAbstractTask task = new MyAbstractTask(dependencies);

    assertThat(task.dependencies()).isSameAs(dependencies);
  }

  public static class MyAbstractTask extends AbstractTask {

    public MyAbstractTask(Map<String, Task> dependencies) {
      super(dependencies);
    }

    public MyAbstractTask(Object result, Map<String, Task> dependencies) {
      super(result, dependencies);
    }

    public void setMyResult(Object result) {
      setResult(result);
    }

    @Override
    public void calculateResult(ProblemsListener problems, Path tempDir) {}

  }
}
