package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.testing.TestingFile;

import com.google.common.collect.ImmutableSet;

public class FileSetTaskTest {
  File file1 = new TestingFile(path("my/file1"));
  File file2 = new TestingFile(path("my/file2"));

  Task task1 = new PrecalculatedTask(file1);
  Task task2 = new PrecalculatedTask(file2);

  FileSetTask fileSetTask = new FileSetTask(ImmutableSet.of(task1, task2));

  @Test
  public void dependencies() {
    assertThat(fileSetTask.dependencies()).containsOnly(task1, task2);
  }

  @Test
  public void execute() {
    fileSetTask.execute(null);
    assertThat((FileSet) fileSetTask.result()).containsOnly(file1, file2);
  }

}
