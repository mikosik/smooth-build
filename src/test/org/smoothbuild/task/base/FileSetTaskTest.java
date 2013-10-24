package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.base.TaskTester.hashes;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

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
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.testing.type.impl.FileTester;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

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
    FileSetTask fileSetTask = new FileSetTask(hashes(task1, task2), codeLocation);
    assertThat(fileSetTask.dependencies()).containsOnly(task1.hash(), task2.hash());
  }

  @Test
  public void execute() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(hashes(task1, task2), codeLocation);

    fileSetTask.execute(sandbox, hashedTasks(task1, task2));

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
    ImmutableList<HashCode> hashes = ImmutableList.of(task1.hash(), task1.hash());
    FileSetTask fileSetTask = new FileSetTask(hashes, codeLocation);

    fileSetTask.execute(sandbox, hashedTasks(task1, task2));

    sandbox.messages().assertOnlyProblem(DuplicatePathError.class);
  }

  @Test
  public void hashOfEmptyFileSetIsDifferentFromHashOfFileSetWithOneElement() throws Exception {
    FileSetTask fileSetTask1 = new FileSetTask(hashes(task1), codeLocation);
    FileSetTask fileSetTask2 = new FileSetTask(hashes(), codeLocation);

    assertThat(fileSetTask1.hash()).isNotEqualTo(fileSetTask2.hash());
  }

  @Test
  public void hashOfFileSetWithOneFileIsDifferentFromHashOfFileSetWithDifferentElement()
      throws Exception {
    FileSetTask fileSetTask1 = new FileSetTask(hashes(task1), codeLocation);
    FileSetTask fileSetTask2 = new FileSetTask(hashes(task2), codeLocation);

    assertThat(fileSetTask1.hash()).isNotEqualTo(fileSetTask2.hash());
  }

  @Test
  public void hashOfFileSetWithOneFileIsDifferentFromHashOfFileSetWithAdditionalFile()
      throws Exception {
    FileSetTask fileSetTask1 = new FileSetTask(hashes(task1), codeLocation);
    FileSetTask fileSetTask2 = new FileSetTask(hashes(task1, task2), codeLocation);

    assertThat(fileSetTask1.hash()).isNotEqualTo(fileSetTask2.hash());
  }
}
