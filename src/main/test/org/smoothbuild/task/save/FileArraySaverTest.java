package org.smoothbuild.task.save;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.SmoothConstants.OBJECTS_DIR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.testing.io.fs.base.FileSystems.fileContent;
import static org.smoothbuild.testing.message.ContainsOnlyMessageMatcher.containsOnlyMessage;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.SubFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.task.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class FileArraySaverTest {
  private final Name name = name("name");
  private final MemoryFileSystem fileSystem = new MemoryFileSystem();
  private final FakeObjectsDb objectsDb = new FakeObjectsDb(new SubFileSystem(fileSystem,
      OBJECTS_DIR));
  private final LoggedMessages messages = new LoggedMessages();

  private FileArraySaver fileArraySaver;
  private Array<SFile> array;
  private SFile file1;
  private SFile file2;

  @Test
  public void duplicated_file_paths_causes_error() throws Exception {
    given(file1 = objectsDb.file(path("abc")));
    given(fileArraySaver = new FileArraySaver(fileSystem, messages));
    given(array = objectsDb.array(SFile.class, file1, file1));
    when(fileArraySaver).save(name, array);
    then(messages, containsOnlyMessage(DuplicatePathsInFileArrayArtifactError.class));
  }

  @Test
  public void all_files_are_stored() throws Exception {
    given(file1 = objectsDb.file(path("abc")));
    given(file2 = objectsDb.file(path("def")));
    given(array = objectsDb.array(SFile.class, file1, file2));
    given(fileArraySaver = new FileArraySaver(fileSystem, messages));
    when(fileArraySaver).save(name, array);
    thenEqual(fileContent(fileSystem, artifactPath(name).append(path("abc"))), "abc");
    thenEqual(fileContent(fileSystem, artifactPath(name).append(path("def"))), "def");
    then(messages, emptyIterable());
  }
}
