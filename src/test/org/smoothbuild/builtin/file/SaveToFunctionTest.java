package org.smoothbuild.builtin.file;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.rootPath;

import org.junit.Test;
import org.smoothbuild.builtin.file.SaveToFunction.Parameters;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.builtin.file.err.PathIsNotADirError;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.plugin.internal.StoredFile;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFileSystem;

public class SaveToFunctionTest {
  TestingSandbox sandbox = new TestingSandbox();
  TestingFileSystem fileSystem = sandbox.projectFileSystem();

  @Test
  public void missingDirArgIsReported() throws Exception {
    runExecute(params(mock(File.class), null));
    sandbox.problems().assertOnlyProblem(MissingRequiredArgError.class);
  }

  @Test
  public void illegalPathsAreReported() {
    for (String path : PathTest.listOfInvalidPaths()) {
      sandbox = new TestingSandbox();
      runExecute(params(mock(File.class), path));
      sandbox.problems().assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void nonDirPathIsReported() throws Exception {
    Path file = path("some/path/file.txt");
    fileSystem.createEmptyFile(file);

    runExecute(params(mock(File.class), file.value()));
    sandbox.problems().assertOnlyProblem(PathIsNotADirError.class);
  }

  @Test
  public void nonDirPathAfterMergingDirAndFileIsReported() throws Exception {
    Path destinationDir = path("root/path/");
    Path filePath = path("file/path/file.txt");
    fileSystem.createEmptyFile(path("root/path/file/path"));

    StoredFile file = new StoredFile(new SubFileSystem(fileSystem, rootPath()), filePath);

    runExecute(params(file, destinationDir.value()));
    sandbox.problems().assertOnlyProblem(PathIsNotADirError.class);
  }

  @Test
  public void execute() throws Exception {
    Path destinationDir = path("root/path");
    Path root = path("file/root");
    Path path = path("file/path/file.txt");

    TestingFile file = new TestingFile(new SubFileSystem(fileSystem, root), path);
    file.createContentWithFilePath();

    runExecute(params(file, destinationDir.value()));

    fileSystem.assertFileContainsItsPath(destinationDir, path);
  }

  private static Parameters params(final File file, final String dir) {
    return new SaveToFunction.Parameters() {
      @Override
      public File file() {
        return file;
      }

      @Override
      public String dir() {
        return dir;
      }
    };
  }

  private void runExecute(Parameters params) {
    SaveToFunction.execute(sandbox, params);
  }
}
