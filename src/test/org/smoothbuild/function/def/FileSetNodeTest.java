package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.PrecalculatedTask;
import org.smoothbuild.task.Task;
import org.smoothbuild.testing.task.TestSandbox;
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
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  ImmutableList<DefinitionNode> elemNodes = ImmutableList.of(fileNode(file1), fileNode(file2));
  FileSetNode fileSetNode = new FileSetNode(elemNodes, codeLocation);

  @Test
  public void type() {
    assertThat(fileSetNode.type()).isEqualTo(FILE_SET);
  }

  @Test
  public void generateTask() throws Exception {
    file1.createContentWithFilePath();
    file2.createContentWithFilePath();

    Task task = fileSetNode.generateTask();
    task.execute(sandbox);

    FileSet result = (FileSet) task.result();
    Iterator<File> it = result.iterator();
    File res1 = it.next();
    File res2 = it.next();
    assertThat(it.hasNext()).isFalse();

    assertThat(Arrays.asList(res1.path(), res2.path())).containsOnly(path1, path2);
    FileTester.assertContentContainsFilePath(res1);
    FileTester.assertContentContainsFilePath(res2);
  }

  private static DefinitionNode fileNode(File file) {
    DefinitionNode fileNode = mock(DefinitionNode.class);
    when(fileNode.generateTask()).thenReturn(new PrecalculatedTask(file));
    return fileNode;
  }
}
