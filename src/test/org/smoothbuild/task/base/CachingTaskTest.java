package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.db.hash.Hash;
import org.smoothbuild.db.task.CachedResult;
import org.smoothbuild.db.task.TaskDb;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class CachingTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  Value value = mock(Value.class);
  HashCode hash = Hash.string("abc");
  String taskName = "name";
  CodeLocation codeLocation = new FakeCodeLocation();

  TaskDb taskDb = mock(TaskDb.class);
  NativeCallHasher nativeCallHasher = mock(NativeCallHasher.class);
  Task task = mock(Task.class);

  CachingTask cachingTask = new CachingTask(taskDb, nativeCallHasher, task);

  @Test
  public void null_result_db_is_forbidden() throws Exception {
    when($cachingTask(null, nativeCallHasher, task));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_native_call_hasher_is_forbidden() throws Exception {
    when($cachingTask(taskDb, null, task));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_task_is_forbidden() throws Exception {
    when($cachingTask(taskDb, nativeCallHasher, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void name_of_wrapped_task_is_returned() throws Exception {
    BDDMockito.given(task.name()).willReturn(taskName);
    assertThat(cachingTask.name()).isEqualTo(taskName);
  }

  @Test
  public void is_internal_forwards_negative_result_from_wrapped_task() throws Exception {
    BDDMockito.given(task.isInternal()).willReturn(false);

    given(cachingTask = new CachingTask(taskDb, nativeCallHasher, task));
    when(cachingTask.isInternal());
    thenReturned(false);
  }

  @Test
  public void is_internal_forwards_positive_result_from_wrapped_task() throws Exception {
    BDDMockito.given(task.isInternal()).willReturn(true);

    given(cachingTask = new CachingTask(taskDb, nativeCallHasher, task));
    when(cachingTask.isInternal());
    thenReturned(true);
  }

  @Test
  public void task_is_executed_when_result_db_does_not_contain_its_result() {
    BDDMockito.given(nativeCallHasher.hash()).willReturn(hash);
    BDDMockito.given(taskDb.contains(hash)).willReturn(false);
    BDDMockito.given(task.execute(sandbox)).willReturn(value);

    assertThat(cachingTask.execute(sandbox)).isEqualTo(value);
  }

  @Test
  public void task_is_not_executed_when_result_from_db_is_returned() throws Exception {
    BDDMockito.given(nativeCallHasher.hash()).willReturn(hash);
    BDDMockito.given(taskDb.contains(hash)).willReturn(true);
    BDDMockito.given(taskDb.read(hash)).willReturn(new CachedResult(value, Empty.messageList()));

    assertThat(cachingTask.execute(sandbox)).isEqualTo(value);
    verifyZeroInteractions(task);
  }

  private static Closure $cachingTask(final TaskDb taskDb, final NativeCallHasher nativeCallHasher,
      final Task task) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachingTask(taskDb, nativeCallHasher, task);
      }
    };
  }
}
