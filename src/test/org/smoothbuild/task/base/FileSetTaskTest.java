package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.plugin.FileSetMatchers.containsFileContainingItsPath;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.task.base.err.DuplicatePathError;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class FileSetTaskTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeSandbox sandbox = new FakeSandbox(fileSystem);
  CodeLocation codeLocation = new FakeCodeLocation();
  Path path1 = path("my/file1");
  Path path2 = path("my/file2");

  File file1;
  File file2;
  Result result1;
  Result result2;

  @Before
  public void before() {
    file1 = sandbox.objectDb().createFileContainingItsPath(path1);
    file2 = sandbox.objectDb().createFileContainingItsPath(path2);
    result1 = new FakeResult(file1);
    result2 = new FakeResult(file2);
  }

  @Test
  public void execute() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(result1, result2), codeLocation);

    FileSet result = (FileSet) fileSetTask.execute(sandbox);

    assertThat(result, containsFileContainingItsPath(path1));
    assertThat(result, containsFileContainingItsPath(path2));
  }

  @Test
  public void duplicatedFileCausesError() throws IOException {
    FileSetTask fileSetTask = new FileSetTask(newArrayList(result1, result1), codeLocation);
    fileSetTask.execute(sandbox);
    sandbox.messages().assertOnlyProblem(DuplicatePathError.class);
  }
}
