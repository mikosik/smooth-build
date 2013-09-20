package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.FileFunction.Parameters;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotAFileError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.plugin.api.PluginErrorException;
import org.smoothbuild.testing.plugin.internal.FileTester;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class FileFunctionTest {
  TestSandbox sandbox = new TestSandbox();

  @Test
  public void illegalPathIsReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestSandbox();
      try {
        runExecute(params(path));
        fail("exception should be thrown");
      } catch (PluginErrorException e) {
        // expected
        assertThat(e.error()).isInstanceOf(IllegalPathError.class);
      }
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    runExecute(params("some/path/file.txt"));
    sandbox.messages().assertOnlyProblem(NoSuchPathError.class);
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    Path dir = path("some/path/");
    Path file = dir.append(path("file.txt"));
    sandbox.projectFileSystem().createEmptyFile(file);

    runExecute(params(dir.value()));
    sandbox.messages().assertOnlyProblem(PathIsNotAFileError.class);
  }

  @Test
  public void execute() throws Exception {
    Path filePath = path("file/path/file.txt");
    sandbox.projectFileSystem().createFileContainingItsPath(filePath);

    File file = runExecute(params(filePath.value()));

    assertThat(file.path()).isEqualTo(filePath);
    FileTester.assertContentContainsFilePath(file);
  }

  private static FileFunction.Parameters params(final String path) {
    return new FileFunction.Parameters() {
      @Override
      public String path() {
        return path;
      }
    };
  }

  private File runExecute(Parameters params) {
    return FileFunction.execute(sandbox, params);
  }
}
