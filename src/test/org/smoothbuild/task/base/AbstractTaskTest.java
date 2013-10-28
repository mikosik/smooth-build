package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.object.Hashed;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.testing.plugin.FakeString;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;

public class AbstractTaskTest {
  @Test
  public void location() throws Exception {
    assertThat(new MyAbstractTask().location()).isEqualTo(MyAbstractTask.LOCATION);
  }

  @Test
  public void resultNotSetInitiallyWhenConstructorWithoutResultParamUsed() {
    MyAbstractTask task = new MyAbstractTask();
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void resultIsSetAfterSetting() throws Exception {
    doTestResultSetting(new FakeString("result"));
  }

  @Test
  public void resultIsSetAfterSettingNull() throws Exception {
    doTestResultSetting(null);
  }

  private void doTestResultSetting(Hashed result) {
    MyAbstractTask task = new MyAbstractTask();

    task.setMyResult(result);

    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }

  public static class MyAbstractTask extends AbstractTask {
    public static final CallLocation LOCATION = CallLocation.callLocation(simpleName("name"),
        codeLocation(1, 2, 4));

    public MyAbstractTask() {
      super(LOCATION);
    }

    public void setMyResult(Hashed result) {
      setResult(result);
    }

    @Override
    public void execute(Sandbox sandbox) {}

    @Override
    public ImmutableCollection<Task> dependencies() {
      return Empty.taskList();
    }
  }
}
