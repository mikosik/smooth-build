package org.smoothbuild.builtin.compress;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.db.objects.ValueCreators.blob;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.common.ZipTester;

public class UnzipperTest {
  private String fileName1;
  private String fileName2;
  private String directoryName;
  private List<SFile> files;
  private Blob blob;
  private Unzipper unzipper;

  @Before
  public void before() {
    givenTest(this);
    given(fileName1 = "file/path/file1.txt");
    given(fileName2 = "file/path/file2.txt");
    given(directoryName = "my/directory/");
    given(unzipper = new Unzipper(containerImpl()));
  }

  @Test
  public void unzips_files() throws Exception {
    given(blob = zipped(fileName1, fileName2));
    when(files = iterableToList(unzipper.unzip(blob)));
    then(files, hasSize(2));
    assertContent(files.get(0).content().openInputStream(), files.get(0).path().value());
    assertContent(files.get(1).content().openInputStream(), files.get(1).path().value());
  }

  private static List<SFile> iterableToList(Iterable<SFile> iterable) {
    ArrayList<SFile> result = new ArrayList<>();
    for (SFile file : iterable) {
      result.add(file);
    }
    return result;
  }

  @Test
  public void directory_entries_are_ignored() throws Exception {
    given(blob = zipped(directoryName));
    when(unzipper.unzip(blob));
    thenReturned(emptyIterable());
  }

  @Test
  public void zip_entry_key_cannot_start_with_slash() throws Exception {
    given(blob = zipped("/leading/slash/is/forbidden"));
    when(unzipper).unzip(blob);
    thenThrown(IllegalPathInZipError.class);
  }

  private static Blob zipped(String... fileNames) throws IOException {
    FileSystem fileSystem = new MemoryFileSystem();
    Path path = ZipTester.zippedFiles(fileSystem, fileNames);
    return blob(objectsDb(), inputStreamToBytes(fileSystem.openInputStream(path)));
  }
}
