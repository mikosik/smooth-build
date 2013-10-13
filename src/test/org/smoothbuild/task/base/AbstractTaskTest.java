package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.base.AbstractTask;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableCollection;
import com.google.common.hash.HashCode;

public class AbstractTaskTest {
  @Test
  public void location() throws Exception {
    assertThat(new MyAbstractTask().location()).isEqualTo(MyAbstractTask.LOCATION);
  }

  @Test
  public void hash() throws Exception {
    assertThat(new MyAbstractTask().hash()).isEqualTo(MyAbstractTask.HASH);
  }

  @Test
  public void resultNotSetInitiallyWhenConstructorWithoutResultParamUsed() {
    MyAbstractTask task = new MyAbstractTask();
    assertThat(task.isResultCalculated()).isFalse();
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
    MyAbstractTask task = new MyAbstractTask();

    task.setMyResult(result);

    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }

  public static class MyAbstractTask extends AbstractTask {
    public static final CallLocation LOCATION = CallLocation.callLocation(simpleName("name"),
        codeLocation(1, 2, 4));
    public static final HashCode HASH = HashCode.fromInt(33);

    public MyAbstractTask() {
      super(LOCATION, HASH);
    }

    public void setMyResult(Object result) {
      setResult(result);
    }

    @Override
    public void execute(Sandbox sandbox, HashedTasks hashedTasks) {}

    @Override
    public ImmutableCollection<HashCode> dependencies() {
      return Empty.hashCodeList();
    }
  }
}
