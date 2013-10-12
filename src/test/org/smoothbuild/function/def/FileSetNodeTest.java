package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.HashedTasksTester.hashedTasks;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.ImmutableList;

public class FileSetNodeTest {
  TestSandbox sandbox = new TestSandbox();
  Path path1 = path("my/file1");
  Path path2 = path("my/file2");
  TestFile file1 = sandbox.resultFileSet().createFile(path1);
  TestFile file2 = sandbox.resultFileSet().createFile(path2);
  TestTask task1 = new TestTask(file1);
  TestTask task2 = new TestTask(file2);

  CodeLocation codeLocation = codeLocation(1, 2, 4);
  DefinitionNode node1 = mock(DefinitionNode.class);
  DefinitionNode node2 = mock(DefinitionNode.class);

  ImmutableList<DefinitionNode> elemNodes = ImmutableList.of(node1, node2);
  FileSetNode fileSetNode = new FileSetNode(elemNodes, codeLocation);

  @Test
  public void type() {
    assertThat(fileSetNode.type()).isEqualTo(FILE_SET);
  }

  @Test
  public void generateTask() throws Exception {
    file1.createContentWithFilePath();
    file2.createContentWithFilePath();

    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    Mockito.when(taskGenerator.generateTask(node1)).thenReturn(task1.hash());
    Mockito.when(taskGenerator.generateTask(node2)).thenReturn(task2.hash());

    Task task = fileSetNode.generateTask(taskGenerator);
    task.execute(sandbox, hashedTasks(task1, task2));

    FileSet result = (FileSet) task.result();
    Iterator<File> it = result.iterator();
    File res1 = it.next();
    File res2 = it.next();
    assertThat(it.hasNext()).isFalse();

    assertThat(Arrays.asList(res1.path(), res2.path())).containsOnly(path1, path2);
    FileTester.assertContentContainsFilePath(res1);
    FileTester.assertContentContainsFilePath(res2);
  }
}
