package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.HashedTasksTester.hashedTasks;
import static org.smoothbuild.testing.task.TaskTester.hashes;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.FileSetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.err.DuplicatePathError;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class FileSetTaskTest {
  TestFileSystem fileSystem = new TestFileSystem();
  TestSandbox sandbox = new TestSandbox(fileSystem);
  Path path1 = path("my/file1");
  Path path2 = path("my/file2");
  TestFile file1 = fileSystem.createFileContainingItsPath(path1);
  TestFile file2 = fileSystem.createFileContainingItsPath(path2);
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  Task task1 = new TestTask(file1);
  Task task2 = new TestTask(file2);
  HashedTasks hashedTasks = hashedTasks(task1, task2);

  FileSetTask fileSetTask = new FileSetTask(hashes(task1, task2), codeLocation);

  public FileSetTaskTest() throws IOException {}

  @Test
  public void dependencies() {
    assertThat(fileSetTask.dependencies()).containsOnly(task1.hash(), task2.hash());
  }

  @Test
  public void execute() throws IOException {
    fileSetTask.execute(sandbox, hashedTasks);

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

    fileSetTask.execute(sandbox, hashedTasks);

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
