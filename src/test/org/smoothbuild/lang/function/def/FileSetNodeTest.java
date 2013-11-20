package org.smoothbuild.lang.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.ImmutableList;

public class FileSetNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakeSandbox sandbox = new FakeSandbox();
  CodeLocation codeLocation = new FakeCodeLocation();

  Node node1 = mock(Node.class);
  Node node2 = mock(Node.class);

  ImmutableList<Node> elemNodes = ImmutableList.of(node1, node2);
  FileSetNode fileSetNode = new FileSetNode(elemNodes, codeLocation);

  @Test
  public void type() {
    assertThat(fileSetNode.type()).isEqualTo(FILE_SET);
  }

  @Test
  public void code_location() throws Exception {
    given(fileSetNode = new FileSetNode(elemNodes, codeLocation));
    when(fileSetNode.codeLocation());
    thenReturned(codeLocation);
  }

  @Test
  public void generateTask() throws Exception {
    File file1 = sandbox.objectDb().createFileContainingItsPath(path("my/file1"));
    File file2 = sandbox.objectDb().createFileContainingItsPath(path("my/file2"));

    Result result1 = new FakeResult(file1);
    Result result2 = new FakeResult(file2);

    Mockito.when(taskGenerator.generateTask(node1)).thenReturn(result1);
    Mockito.when(taskGenerator.generateTask(node2)).thenReturn(result2);

    Task task = fileSetNode.generateTask(taskGenerator);
    FileSet result = (FileSet) task.execute(sandbox);

    assertThat(result).containsOnly(file1, file2);
  }
}
