package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.task.base.Task;

import com.google.inject.util.Providers;

public class TaskExecutorTest {
  Task task = mock(Task.class);
  SValue value = mock(SValue.class);
  LoggedMessages loggedMessages = new LoggedMessages();
  PluginApiImpl pluginApi = mock(PluginApiImpl.class);
  TaskReporter taskReporter = mock(TaskReporter.class);

  TaskExecutor taskExecutor = new TaskExecutor(Providers.of(pluginApi), taskReporter);

  @Before
  public void before() {
    Mockito.when(pluginApi.loggedMessages()).thenReturn(loggedMessages);
    Mockito.when(task.execute(pluginApi)).thenReturn(value);
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
