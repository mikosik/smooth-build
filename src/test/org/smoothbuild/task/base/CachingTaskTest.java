package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.task.CachedResult;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class CachingTaskTest {
  FakeNativeApi nativeApi = new FakeNativeApi();
  SString stringValue = mock(SString.class);
  SString stringValue2 = mock(SString.class);
  HashCode hash = Hash.string("abc");
  CodeLocation codeLocation = new FakeCodeLocation();

  TaskDb taskDb = mock(TaskDb.class);
  @SuppressWarnings("unchecked")
  CallHasher<SString> callHasher = mock(CallHasher.class);
  Task<SString> task = new StringTask(stringValue, codeLocation);

  CachingTask<?> cachingTask;

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
    given(cachingTask = new CachingTask<>(taskDb, callHasher, task));
    when(cachingTask.name());
    thenReturned(task.name());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void is_internal_forwards_negative_result_from_wrapped_task() throws Exception {
    given(task = mock(Task.class));
    given(willReturn("name"), task).name();
    given(willReturn(false), task).isInternal();
    given(willReturn(STRING), task).resultType();
    given(willReturn(codeLocation), task).codeLocation();
    given(cachingTask = new CachingTask<>(taskDb, callHasher, task));
    when(cachingTask.isInternal());
    thenReturned(false);
  }

  @Test
  public void is_internal_forwards_positive_result_from_wrapped_task() throws Exception {
    given(cachingTask = new CachingTask<>(taskDb, callHasher, task));
    when(cachingTask.isInternal());
    thenReturned(true);
  }

  @Test
  public void task_is_executed_when_result_db_does_not_contain_its_result() {
    given(willReturn(hash), callHasher).hash();
    given(willReturn(false), taskDb).contains(hash);
    given(cachingTask = new CachingTask<>(taskDb, callHasher, task));
    when(cachingTask.execute(nativeApi));
    thenReturned(stringValue);
  }

  @Test
  public void task_is_not_executed_when_result_from_db_is_returned() throws Exception {
    given(willReturn(hash), callHasher).hash();
    given(willReturn(true), taskDb).contains(hash);
    given(willReturn(new CachedResult<>(stringValue2, Empty.messageList())), taskDb).read(hash,
        STRING);
    given(cachingTask = new CachingTask<>(taskDb, callHasher, task));
    assertThat(cachingTask.execute(nativeApi)).isEqualTo(stringValue2);
  }

  private static <T extends SValue> Closure $cachingTask(final TaskDb taskDb,
      final CallHasher<T> callHasher, final Task<T> task) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachingTask<>(taskDb, callHasher, task);
      }
    };
  }
}
