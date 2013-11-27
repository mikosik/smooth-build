package org.smoothbuild.task.exec.save;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.task.exec.save.Savers.artifactPath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;
import static org.smoothbuild.testing.lang.type.FakeArray.fakeArray;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.task.exec.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.message.FakeMessageGroup;

public class FileArraySaverTest {
  Name name = name("name");
  SFile file1 = new FakeFile(path("abc"));
  SFile file2 = new FakeFile(path("def"));

  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeMessageGroup messageGroup = new FakeMessageGroup();

  FileArraySaver fileArraySaver;

  @Test
  public void duplicated_file_paths_causes_error() throws Exception {
    fileSystem.createFile(targetPath(file1.content()), "ignored");

    fileArraySaver = new FileArraySaver(fileSystem, messageGroup);
    fileArraySaver.save(name, fakeArray(FILE_ARRAY, file1, file1));
    messageGroup.assertOnlyProblem(DuplicatePathsInFileArrayArtifactError.class);
  }

  @Test
  public void all_files_are_stored() throws Exception {
    fileSystem.createFile(targetPath(file1.content()), file1.path().value());
    fileSystem.createFile(targetPath(file2.content()), file2.path().value());

    fileArraySaver = new FileArraySaver(fileSystem, messageGroup);
    fileArraySaver.save(name, fakeArray(FILE_ARRAY, file1, file2));

    fileSystem.assertFileContains(artifactPath(name).append(file1.path()), file1.path().value());
    fileSystem.assertFileContains(artifactPath(name).append(file2.path()), file2.path().value());

    messageGroup.assertNoProblems();
  }
}
