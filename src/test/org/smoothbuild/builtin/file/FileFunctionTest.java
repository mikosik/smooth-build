package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.FileImplTest.assertContentHasFilePath;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.FileFunction.Parameters;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotAFileError;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.PathTest;
import org.smoothbuild.plugin.TestingSandbox;

public class FileFunctionTest {
  TestingSandbox sandbox = new TestingSandbox();

  @Test
  public void missingPathArgIsReported() throws Exception {
    runExecute(params(null));
    sandbox.problems().assertOnlyProblem(MissingRequiredArgError.class);
  }

  @Test
  public void illegalPathIsReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestingSandbox();
      runExecute(params(path));
      sandbox.problems().assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    runExecute(params("some/path/file.txt"));
    sandbox.problems().assertOnlyProblem(NoSuchPathError.class);
  }

  @Test
  public void nonFilePathIsReported() throws Exception {
    Path dir = path("some/path/");
    Path file = dir.append(path("file.txt"));
    sandbox.fileSystem().createEmptyFile(file);

    runExecute(params(dir.value()));
    sandbox.problems().assertOnlyProblem(PathIsNotAFileError.class);
  }

  @Test
  public void execute() throws Exception {
    Path filePath = path("file/path/file.txt");
    sandbox.fileSystem().createFileContainingItsPath(Path.rootPath(), filePath);

    File file = runExecute(params(filePath.value()));

    assertThat(file.path()).isEqualTo(filePath);
    assertContentHasFilePath(file);
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
