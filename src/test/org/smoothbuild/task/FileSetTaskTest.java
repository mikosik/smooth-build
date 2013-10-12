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
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
  HashedTasks hashedTasks = new HashedTasks(ImmutableMap.of(task1.hash(), task1, task2.hash(),
      task2));

  FileSetTask fileSetTask = new FileSetTask(ImmutableList.of(task1.hash(), task2.hash()),
      codeLocation);

  public FileSetTaskTest() throws IOException {}

  @Test
  public void dependencies() {
    assertThat(fileSetTask.dependencies()).containsOnly(task1.hash(), task2.hash());
  }

  @Test
  public void execute() throws IOException {
    file1.createContentWithFilePath();
    file2.createContentWithFilePath();

    fileSetTask.execute(sandbox, hashedTasks);

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
    ImmutableList<HashCode> hashes = ImmutableList.of(task1.hash(), task1.hash());
    FileSetTask fileSetTask = new FileSetTask(hashes, codeLocation);

    fileSetTask.execute(sandbox, hashedTasks);

    sandbox.messages().assertOnlyProblem(DuplicatePathError.class);
  }
}
