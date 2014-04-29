package org.smoothbuild.task.save;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import org.junit.Test;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.task.save.FileArraySaver;
import org.smoothbuild.task.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeLoggedMessages;

public class FileArraySaverTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final Name name = name("name");
  private final SFile file1 = objectsDb.file(path("abc"));
  private final SFile file2 = objectsDb.file(path("def"));

  private final FakeFileSystem fileSystem = new FakeFileSystem();
  private final FakeLoggedMessages messages = new FakeLoggedMessages();

  private FileArraySaver fileArraySaver;

  @Test
  public void duplicated_file_paths_causes_error() throws Exception {
    fileSystem.createFile(targetPath(file1.content()), "ignored");

    fileArraySaver = new FileArraySaver(fileSystem, messages);
    fileArraySaver.save(name, objectsDb.array(FILE_ARRAY, file1, file1));
    messages.assertContainsOnly(DuplicatePathsInFileArrayArtifactError.class);
  }

  @Test
  public void all_files_are_stored() throws Exception {
    fileSystem.createFile(targetPath(file1.content()), file1.path().value());
    fileSystem.createFile(targetPath(file2.content()), file2.path().value());

    fileArraySaver = new FileArraySaver(fileSystem, messages);
    fileArraySaver.save(name, objectsDb.array(FILE_ARRAY, file1, file2));

    fileSystem.assertFileContains(artifactPath(name).append(file1.path()), file1.path().value());
    fileSystem.assertFileContains(artifactPath(name).append(file2.path()), file2.path().value());

    messages.assertNoProblems();
  }
}
