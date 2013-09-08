package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.TestingFile.assertContentContainsFilePath;

import org.junit.Test;
import org.smoothbuild.builtin.file.FilesFunction.Parameters;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;

public class FilesFunctionTest {
  TestingSandbox sandbox = new TestingSandbox();

  @Test
  public void missingDirArgIsReported() throws Exception {
    runExecute(params(null));
    sandbox.problems().assertOnlyProblem(MissingRequiredArgError.class);
  }

  @Test
  public void illegalPathsAreReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestingSandbox();
      runExecute(params(path));
      sandbox.problems().assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    runExecute(params("some/path"));
    sandbox.problems().assertOnlyProblem(NoSuchPathError.class);
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path filePath = path("some/path/file.txt");
    sandbox.projectFileSystem().createEmptyFile(filePath);

    runExecute(params(filePath.value()));
    sandbox.problems().assertOnlyProblem(PathIsNotADirError.class);
  }

  @Test
  public void execute() throws Exception {
    Path rootPath = path("root/path");
    Path filePath = path("file/path/file.txt");
    sandbox.projectFileSystem().subFileSystem(rootPath).createFileContainingItsPath(filePath);

    FileSet fileSet = runExecute(params(rootPath.value()));

    assertContentContainsFilePath(fileSet.file(filePath));
  }

  private static FilesFunction.Parameters params(final String dir) {
    return new FilesFunction.Parameters() {
      @Override
      public String dir() {
        return dir;
      }
    };
  }

  private FileSet runExecute(Parameters params) {
    return FilesFunction.execute(sandbox, params);
  }
}
