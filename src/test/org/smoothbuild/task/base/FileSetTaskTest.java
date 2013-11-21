package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class FileSetTaskTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeSandbox sandbox = new FakeSandbox(fileSystem);
  CodeLocation codeLocation = new FakeCodeLocation();

  File file1;
  File file2;
  Result result1;
  Result result2;

  @Before
  public void before() {
    file1 = sandbox.objectDb().createFileContainingItsPath(path("my/file1"));
    file2 = sandbox.objectDb().createFileContainingItsPath(path("my/file2"));
    result1 = new FakeResult(file1);
    result2 = new FakeResult(file2);
  }

  @Test
  public void execute() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(result1, result2), codeLocation);
    @SuppressWarnings("unchecked")
    Array<File> result = (Array<File>) fileSetTask.execute(sandbox);

    sandbox.messages().assertNoProblems();
    assertThat(result).containsOnly(file1, file2);
  }

  @Test
  public void duplicated_files_are_allowed() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(result1, result1), codeLocation);
    @SuppressWarnings("unchecked")
    Array<File> result = (Array<File>) fileSetTask.execute(sandbox);

    sandbox.messages().assertNoProblems();
    assertThat(result).containsOnly(file1, file1);
  }
}
