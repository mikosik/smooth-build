package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

public class AbstractTaskTest {

  @Test
  public void resultNotSetInitiallyWhenConstructorWithoutResultParamUsed() {
    MyAbstractTask task = new MyAbstractTask();
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void resultSetInitiallyWhenConstructorWithResultParamUsed() {
    String result = "result";
    MyAbstractTask task = new MyAbstractTask(result);

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

  public static class MyAbstractTask extends AbstractTask {
    public MyAbstractTask() {
      super();
    }

    public MyAbstractTask(Object result) {
      super(result);
    }

    public void setMyResult(Object result) {
      setResult(result);
    }

    @Override
    public void calculateResult(Sandbox sandbox) {}

    @Override
    public ImmutableCollection<Task> dependencies() {
      return Empty.taskList();
    }
  }
}
