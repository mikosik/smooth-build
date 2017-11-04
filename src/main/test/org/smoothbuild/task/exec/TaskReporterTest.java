package org.smoothbuild.task.exec;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.exec.TaskReporter.header;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;

public class TaskReporterTest {
  Console console = mock(Console.class);
  TaskReporter taskReporter = new TaskReporter(console);
  List<Message> messages;
  Task task;

  @Test
  public void internal_task_with_message_is_printed() {
    given(task = createTask(true));
    given(messages = asList(new WarningMessage("message")));
    given(task).setOutput(new Output(messages));
    when(taskReporter).report(task, false);
    thenCalled(console).print(header(task, false), messages);
  }

  @Test
  public void internal_task_without_message_is_not_printed() {
    given(task = createTask(true));
    when(taskReporter).report(task, false);
    thenCalledTimes(0, onInstance(console));
  }

  @Test
  public void non_internal_task_with_message_is_printed() {
    given(messages = asList(new WarningMessage("message")));
    given(task = createTask(false));
    given(task).setOutput(new Output(messages));
    when(taskReporter).report(task, false);
    thenCalled(console).print(header(task, false), messages);
  }

  @Test
  public void non_internal_task_without_message_is_printed() {
    given(task = createTask(false));
    given(messages = asList());
    given(task).setOutput(new Output(messages));
    when(taskReporter).report(task, false);
    thenCalled(console).print(header(task, false), messages);
  }

  private static Task createTask(boolean isInternal) {
    return new Task(new MyEvaluator(isInternal), ImmutableList.of());
  }

  private static final class MyEvaluator extends Evaluator {
    private MyEvaluator(boolean isInternal) {
      super(null, "name", isInternal, true, Location.location("script.smooth", 2),
          ImmutableList.of());
    }

    public Output evaluate(Input input, ContainerImpl container) {
      return null;
    }
  }
}
