package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.FilesFunction.Parameters;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.NoSuchPathError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.testing.plugin.internal.FileTester;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class FilesFunctionTest {
  TestSandbox sandbox = new TestSandbox();

  @Test
  public void illegalPathsAreReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestSandbox();
      runExecute(params(path));
      sandbox.messages().assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void nonexistentPathIsReported() throws Exception {
    runExecute(params("some/path"));
    sandbox.messages().assertOnlyProblem(NoSuchPathError.class);
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path filePath = path("some/path/file.txt");
    sandbox.projectFileSystem().createEmptyFile(filePath);

    runExecute(params(filePath.value()));
    sandbox.messages().assertOnlyProblem(PathIsNotADirError.class);
  }

  @Test
  public void execute() throws Exception {
    Path rootPath = path("root/path");
    Path filePath = path("file/path/file.txt");
    sandbox.projectFileSystem().subFileSystem(rootPath).createFileContainingItsPath(filePath);

    FileSet fileSet = runExecute(params(rootPath.value()));

    FileTester.assertContentContainsFilePath(fileSet.file(filePath));
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
