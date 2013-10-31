package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.testing.plugin.FileSetMatchers.containsFileContainingItsPath;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.ImmutableList;

public class FileSetNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakeSandbox sandbox = new FakeSandbox();
  Path path1 = path("my/file1");
  Path path2 = path("my/file2");

  LocatedNode node1 = mock(LocatedNode.class);
  LocatedNode node2 = mock(LocatedNode.class);

  ImmutableList<LocatedNode> elemNodes = ImmutableList.of(node1, node2);
  FileSetNode fileSetNode = new FileSetNode(elemNodes);

  @Test
  public void type() {
    assertThat(fileSetNode.type()).isEqualTo(FILE_SET);
  }

  @Test
  public void generateTask() throws Exception {
    File file1 = sandbox.objectDb().createFileContainingItsPath(path1);
    File file2 = sandbox.objectDb().createFileContainingItsPath(path2);

    Result result1 = new FakeResult(file1);
    Result result2 = new FakeResult(file2);

    Mockito.when(taskGenerator.generateTask(node1)).thenReturn(result1);
    Mockito.when(taskGenerator.generateTask(node2)).thenReturn(result2);

    Task task = fileSetNode.generateTask(taskGenerator);
    FileSet result = (FileSet) task.execute(sandbox);

    assertThat(result, containsFileContainingItsPath(path1));
    assertThat(result, containsFileContainingItsPath(path2));
  }
}
