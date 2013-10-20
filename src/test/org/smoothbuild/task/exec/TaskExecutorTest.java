package org.smoothbuild.task.exec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;
import org.smoothbuild.object.HashedDb;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.api.MutableFile;

import com.google.common.hash.HashCode;

public class TaskExecutorTest {
  Task task1 = task(1);
  FakeFileSystem fileSystem = new FakeFileSystem();
  HashedTasks hashedTasks = hashedTasks(task1);
  FakeUserConsole userConsole = new FakeUserConsole();
  ObjectsDb objectsDb = new ObjectsDb(new HashedDb(fileSystem));

  TaskExecutor taskExecutor = new TaskExecutor(fileSystem, objectsDb, hashedTasks, userConsole);

  @Test
  public void task_with_result_already_calculate_is_not_executed() {
    when(task1.isResultCalculated()).thenReturn(true);
    taskExecutor.execute(task1.hash());
    verify(task1, times(0)).execute(Matchers.<Sandbox> any(), Matchers.<HashedTasks> any());
  }

  @Test
  public void task_with_result_not_calculated_is_executed() {
    when(task1.isResultCalculated()).thenReturn(false);
    taskExecutor.execute(task1.hash());
    verify(task1).execute(Matchers.<Sandbox> any(), Matchers.eq(hashedTasks));
  }

  @Test
  public void results_can_be_stored_via_sandbox() throws Exception {
    final Path filePath = path("my/file");
    final String content = "content";
    when(task1.isResultCalculated()).thenReturn(false);
    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Sandbox sandbox = (Sandbox) invocation.getArguments()[0];
        MutableFile file = sandbox.createFile(filePath);
        FileTester.createContent(file, content);
        return null;
      }
    }).when(task1).execute(Matchers.<Sandbox> any(), Matchers.<HashedTasks> any());

    taskExecutor.execute(task1.hash());

    Path fullPath = BUILD_DIR.append(path(task1.hash().toString())).append(filePath);
    fileSystem.assertFileContains(fullPath, content);
  }

  @Test
  public void message_can_be_reported_via_sandbox() throws Exception {
    when(task1.isResultCalculated()).thenReturn(false);
    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Sandbox sandbox = (Sandbox) invocation.getArguments()[0];
        sandbox.report(new Message(MessageType.ERROR, "message"));
        return null;
      }
    }).when(task1).execute(Matchers.<Sandbox> any(), Matchers.<HashedTasks> any());

    taskExecutor.execute(task1.hash());

    userConsole.assertProblemsFound();
  }

  private Task task(int value) {
    Task task = mock(Task.class);
    when(task.hash()).thenReturn(HashCode.fromInt(value));
    when(task.location()).thenReturn(callLocation(simpleName("name"), codeLocation(1, 2, 4)));
    return task;
  }
}
