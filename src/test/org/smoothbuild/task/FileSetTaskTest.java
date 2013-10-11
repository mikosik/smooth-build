package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.err.DuplicatePathError;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.ImmutableSet;

public class FileSetTaskTest {
  TestSandbox sandbox = new TestSandbox();
  Path path1 = path("my/file1");
  Path path2 = path("my/file2");
  TestFile file1 = sandbox.resultFileSet().createFile(path1);
  TestFile file2 = sandbox.resultFileSet().createFile(path2);
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  Task task1 = new TestTask(file1);
  Task task2 = new TestTask(file2);

  FileSetTask fileSetTask = new FileSetTask(ImmutableSet.of(task1, task2), codeLocation);

  public FileSetTaskTest() throws IOException {}

  @Test
  public void dependencies() {
    assertThat(fileSetTask.dependencies()).containsOnly(task1, task2);
  }

  @Test
  public void execute() throws IOException {
    file1.createContentWithFilePath();
    file2.createContentWithFilePath();

    fileSetTask.execute(sandbox);

    FileSet result = (FileSet) fileSetTask.result();
    Iterator<File> it = result.iterator();
    File res1 = it.next();
    File res2 = it.next();
    assertThat(it.hasNext()).isFalse();

    assertThat(Arrays.asList(res1.path(), res2.path())).containsOnly(path1, path2);
    FileTester.assertContentContainsFilePath(res1);
    FileTester.assertContentContainsFilePath(res2);
  }

  @Test
  public void duplicatedFileCausesError() throws IOException {
    file1.createContentWithFilePath();
    FileSetTask fileSetTask = new FileSetTask(ImmutableSet.of(task1, task1), codeLocation);

    fileSetTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(DuplicatePathError.class);
  }
}
