package org.smoothbuild.task.exec;

import static org.smoothbuild.task.exec.TaskReporter.header;
import static org.smoothbuild.testing.db.values.ValueCreators.emptyMessageArray;
import static org.smoothbuild.testing.db.values.ValueCreators.messageArrayWithOneError;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.cli.Console;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.message.TestingMessagesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskResult;

import com.google.common.hash.HashCode;

public class TaskReporterTest {
  private final Console console = mock(Console.class);
  private final TaskReporter taskReporter = new TaskReporter(console);
  private final MessagesDb messagesDb = new TestingMessagesDb();
  private Array messages;
  private Task task;

  @Test
  public void internal_task_with_message_is_printed() {
    given(task = createTask(true));
    given(messages = messageArrayWithOneError());
    given(task).setResult(new TaskResult(new Output(null, messages), false));
    when(taskReporter).report(task);
    thenCalled(console).print(header(task), messages);
  }

  @Test
  public void internal_task_without_message_is_not_printed() {
    given(task = createTask(true));
    when(taskReporter).report(task);
    thenCalledTimes(0, onInstance(console));
  }

  @Test
  public void non_internal_task_with_message_is_printed() {
    given(messages = messageArrayWithOneError());
    given(task = createTask(false));
    given(task).setResult(new TaskResult(new Output(null, messages), false));
    when(taskReporter).report(task);
    thenCalled(console).print(header(task), messages);
  }

  @Test
  public void non_internal_task_without_message_is_not_printed() {
    given(task = createTask(false));
    given(messages = emptyMessageArray());
    given(task).setResult(new TaskResult(new Output(null, messages), false));
    when(taskReporter).report(task);
    thenCalledTimes(0, onInstance(console));
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
