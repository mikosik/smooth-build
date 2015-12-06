package org.smoothbuild.task.save;

import static org.smoothbuild.SmoothConstants.VALUES_DIR;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.testing.db.values.ValueCreators.array;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.smoothbuild.testing.io.fs.base.FileSystems.fileContent;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.cli.Console;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.SubFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ExecutionException;

public class FileArraySaverTest {
  private final Name name = name("name");
  private final MemoryFileSystem fileSystem = new MemoryFileSystem();
  private final ValuesDb valuesDb = valuesDb(new SubFileSystem(fileSystem, VALUES_DIR));

  private FileArraySaver fileArraySaver;
  private Array<SFile> array;
  private SFile file1;
  private SFile file2;

  @Test
  public void duplicated_file_paths_causes_exception() throws Exception {
    given(file1 = file(valuesDb, path("abc")));
    given(fileArraySaver = new FileArraySaver(fileSystem, mock(Console.class)));
    given(array = array(valuesDb, SFile.class, file1, file1));
    when(fileArraySaver).save(name, array);
    thenThrown(ExecutionException.class);
  }

  @Test
  public void all_files_are_stored() throws Exception {
    given(file1 = file(valuesDb, path("abc")));
    given(file2 = file(valuesDb, path("def")));
    given(array = array(valuesDb, SFile.class, file1, file2));
    given(fileArraySaver = new FileArraySaver(fileSystem, mock(Console.class)));
    when(fileArraySaver).save(name, array);
    thenEqual(fileContent(fileSystem, artifactPath(name).append(path("abc"))), "abc");
    thenEqual(fileContent(fileSystem, artifactPath(name).append(path("def"))), "def");
  }
}
