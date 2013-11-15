package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.io.db.hash.Hash;
import org.smoothbuild.io.db.task.CachedResult;
import org.smoothbuild.io.db.task.TaskDb;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.lang.plugin.StringValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class CachingTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  StringValue stringValue = mock(StringValue.class);
  StringValue stringValue2 = mock(StringValue.class);
  HashCode hash = Hash.string("abc");
  CodeLocation codeLocation = new FakeCodeLocation();

  TaskDb taskDb = mock(TaskDb.class);
  CallHasher callHasher = mock(CallHasher.class);
  Task task = new StringTask(stringValue, codeLocation);

  CachingTask cachingTask;

  @Test
  public void null_result_db_is_forbidden() throws Exception {
    when($cachingTask(null, callHasher, task));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_native_call_hasher_is_forbidden() throws Exception {
    when($cachingTask(taskDb, null, task));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_task_is_forbidden() throws Exception {
    when($cachingTask(taskDb, callHasher, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void name_of_wrapped_task_is_returned() throws Exception {
    given(cachingTask = new CachingTask(taskDb, callHasher, task));
    when(cachingTask.name());
    thenReturned(task.name());
  }

  @Test
  public void is_internal_forwards_negative_result_from_wrapped_task() throws Exception {
    task = mock(Task.class);
    BDDMockito.given(task.name()).willReturn("name");
    BDDMockito.given(task.isInternal()).willReturn(false);
    BDDMockito.given(task.codeLocation()).willReturn(codeLocation);

    given(cachingTask = new CachingTask(taskDb, callHasher, task));
    when(cachingTask.isInternal());
    thenReturned(false);
  }

  @Test
  public void is_internal_forwards_positive_result_from_wrapped_task() throws Exception {
    given(cachingTask = new CachingTask(taskDb, callHasher, task));
    when(cachingTask.isInternal());
    thenReturned(true);
  }

  @Test
  public void task_is_executed_when_result_db_does_not_contain_its_result() {
    BDDMockito.given(callHasher.hash()).willReturn(hash);
    BDDMockito.given(taskDb.contains(hash)).willReturn(false);

    given(cachingTask = new CachingTask(taskDb, callHasher, task));
    when(cachingTask.execute(sandbox));
    thenReturned(stringValue);
  }

  @Test
  public void task_is_not_executed_when_result_from_db_is_returned() throws Exception {
    BDDMockito.given(callHasher.hash()).willReturn(hash);
    BDDMockito.given(taskDb.contains(hash)).willReturn(true);
    BDDMockito.given(taskDb.read(hash)).willReturn(
        new CachedResult(stringValue2, Empty.messageList()));

    cachingTask = new CachingTask(taskDb, callHasher, task);
    assertThat(cachingTask.execute(sandbox)).isEqualTo(stringValue2);
  }

  private static Closure $cachingTask(final TaskDb taskDb, final CallHasher callHasher,
      final Task task) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachingTask(taskDb, callHasher, task);
      }
    };
  }
}
