package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.task.base.err.DuplicatePathError;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.plugin.FileTester;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class FileSetTaskTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeSandbox sandbox = new FakeSandbox(fileSystem);
  Path path1 = path("my/file1");
  Path path2 = path("my/file2");
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  File file1;
  File file2;
  Task task1;
  Task task2;

  @Before
  public void before() {
    file1 = sandbox.objectDb().createFileContainingItsPath(path1);
    file2 = sandbox.objectDb().createFileContainingItsPath(path2);
    task1 = new FakeTask(file1);
    task2 = new FakeTask(file2);
  }

  @Test
  public void dependencies() {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(task1, task2), codeLocation);
    assertThat(fileSetTask.dependencies()).containsOnly(task1, task2);
  }

  @Test
  public void execute() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(task1, task2), codeLocation);

    fileSetTask.execute(sandbox);

    List<File> result = newArrayList((FileSet) fileSetTask.result());

    assertThat(result.size()).isEqualTo(2);
    File f1 = result.get(0);
    File f2 = result.get(1);
    assertThat(f1.path()).isEqualTo(path1);
    assertThat(f2.path()).isEqualTo(path2);

    FileTester.assertContentContainsFilePath(f1);
    FileTester.assertContentContainsFilePath(f2);
  }

  @Test
  public void duplicatedFileCausesError() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(task1, task1), codeLocation);
    fileSetTask.execute(sandbox);
    sandbox.messages().assertOnlyProblem(DuplicatePathError.class);
  }
}
