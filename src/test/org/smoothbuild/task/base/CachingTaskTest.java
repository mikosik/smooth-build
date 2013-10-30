package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.object.Hash;
import org.smoothbuild.object.ResultDb;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.hash.HashCode;

public class CachingTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  Value value = mock(Value.class);
  HashCode hash = Hash.string("abc");

  ResultDb resultDb = mock(ResultDb.class);
  NativeCallHasher nativeCallHasher = mock(NativeCallHasher.class);
  Task task = mock(Task.class);

  CachingTask cachingTask = new CachingTask(resultDb, nativeCallHasher, task);

  @Test
  public void task_is_executed_when_result_db_does_not_contain_its_result() {
    BDDMockito.given(nativeCallHasher.hash()).willReturn(hash);
    BDDMockito.given(resultDb.contains(hash)).willReturn(false);
    BDDMockito.given(task.execute(sandbox)).willReturn(value);

    assertThat(cachingTask.execute(sandbox)).isEqualTo(value);
  }

  @Test
  public void task_is_not_executed_when_result_from_db_is_returned() throws Exception {
    BDDMockito.given(nativeCallHasher.hash()).willReturn(hash);
    BDDMockito.given(resultDb.contains(hash)).willReturn(true);
    BDDMockito.given(resultDb.read(hash)).willReturn(value);

    assertThat(cachingTask.execute(sandbox)).isEqualTo(value);
    verifyZeroInteractions(task);
  }
}
