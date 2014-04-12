package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.task.base.Task;

import com.google.inject.util.Providers;

public class TaskExecutorTest {
  Task<?> task = mock(Task.class);
  SValue value = mock(SValue.class);
  LoggedMessages loggedMessages = new LoggedMessages();
  NativeApiImpl nativeApi = mock(NativeApiImpl.class);
  TaskReporter taskReporter = mock(TaskReporter.class);

  TaskExecutor taskExecutor = new TaskExecutor(Providers.of(nativeApi), taskReporter);

  @Before
  public void before() {
    given(willReturn(loggedMessages), nativeApi).loggedMessages();
    given(willReturn(value), task).execute(nativeApi);
  }

  @Test
  public void execute_return_value_returned_by_task_execute() {
    assertThat(taskExecutor.execute(task)).isEqualTo(value);
  }

  @Test
  public void error_message_causes_exception() throws Exception {
    loggedMessages.log(new Message(ERROR, ""));
    try {
      taskExecutor.execute(task);
      fail("exception should be thrown");
    } catch (BuildInterruptedException e) {
      // expected
    }
  }
}
