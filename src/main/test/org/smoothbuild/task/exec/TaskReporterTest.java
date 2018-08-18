package org.smoothbuild.task.exec;

import static org.smoothbuild.task.exec.TaskReporter.header;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.cli.Console;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.message.TestingMessagesDb;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;

public class TaskReporterTest {
  private final Console console = mock(Console.class);
  private final TaskReporter taskReporter = new TaskReporter(console);
  private final MessagesDb messagesDb = new TestingMessagesDb();
  private List<Message> messages;
  private Task task;

  @Test
  public void internal_task_with_message_is_printed() {
    given(task = createTask(true));
    given(messages = list(messagesDb.warning("message")));
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
    given(messages = list(messagesDb.warning("message")));
    given(task = createTask(false));
    given(task).setOutput(new Output(messages));
    when(taskReporter).report(task, false);
    thenCalled(console).print(header(task, false), messages);
  }

  @Test
  public void non_internal_task_without_message_is_printed() {
    given(task = createTask(false));
    given(messages = list());
    given(task).setOutput(new Output(messages));
    when(taskReporter).report(task, false);
    thenCalled(console).print(header(task, false), messages);
  }

  private static Task createTask(boolean isInternal) {
    return new Task(new MyEvaluator(isInternal), list(), Hash.integer(13));
  }

  private static final class MyEvaluator extends Evaluator {
    private MyEvaluator(boolean isInternal) {
      super(null, "name", isInternal, true, list(),
          Location.location(Paths.get("script.smooth"), 2));
    }

    @Override
    public Output evaluate(Input input, Container container) {
      return null;
    }

    @Override
    public HashCode hash() {
      return Hash.integer(17);
    }
  }
}
