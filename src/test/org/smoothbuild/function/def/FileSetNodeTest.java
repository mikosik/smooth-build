package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.task.PrecalculatedTask;
import org.smoothbuild.task.Task;
import org.smoothbuild.testing.TestingFile;

import com.google.common.collect.ImmutableList;

public class FileSetNodeTest {

  File file1 = new TestingFile(path("my/file1"));
  File file2 = new TestingFile(path("my/file2"));

  DefinitionNode node1 = fileNode(file1);
  DefinitionNode node2 = fileNode(file2);

  ImmutableList<DefinitionNode> elemNodes = ImmutableList.of(node1, node2);

  FileSetNode fileSetNode = new FileSetNode(elemNodes);

  @Test
  public void type() {
    assertThat(fileSetNode.type()).isEqualTo(FILE_SET);
  }

  private DefinitionNode fileNode(File file) {
    DefinitionNode fileNode = mock(DefinitionNode.class);
    when(fileNode.generateTask()).thenReturn(new PrecalculatedTask(file));
    return fileNode;
  }

  @Test
  public void generateTask() throws Exception {
    Task task = fileSetNode.generateTask();
    task.execute(null);
    assertThat((FileSet) task.result()).containsOnly(file1, file2);
  }
}
