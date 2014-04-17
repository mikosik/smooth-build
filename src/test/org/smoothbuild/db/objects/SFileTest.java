package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SFileTest {
  private final String string = "abc";
  private final String otherString = "def";
  private final Path path = path("path");
  private final Path otherPath = path("other/path");

  private ObjectsDb objectsDb;
  private FileBuilder fileBuilder;
  private SFile file;
  private SFile file2;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void null_path_is_forbidden() throws Exception {
    given(fileBuilder = objectsDb.fileBuilder());
    when(fileBuilder).setPath(null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void creating_file_without_path_fails() throws Exception {
    given(fileBuilder = objectsDb.fileBuilder());
    given(fileBuilder).setContent(createBlob(objectsDb, string));
    when(fileBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void creating_file_without_content_fails() throws Exception {
    given(fileBuilder = objectsDb.fileBuilder());
    given(fileBuilder).setPath(path);
    when(fileBuilder).build();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void setting_path_twice_fails() throws Exception {
    given(fileBuilder = objectsDb.fileBuilder());
    given(fileBuilder).setPath(path);
    when(fileBuilder).setPath(path);
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void setting_content_twice_fails() throws Exception {
    given(fileBuilder = objectsDb.fileBuilder());
    given(fileBuilder).setContent(createBlob(objectsDb, string));
    when(fileBuilder).setContent(createBlob(objectsDb, string));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void type_of_sfile_is_file() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(file).type();
    thenReturned(FILE);
  }

  @Test
  public void path_contains_path_passed_to_builder() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(file).path();
    thenReturned(path);
  }

  @Test
  public void content_contains_data_passed_to_builder() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(inputStreamToString(file.content().openInputStream()));
    thenReturned(string);
  }

  @Test
  public void file_hash_is_different_of_its_content_hash() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(file.hash());
    thenReturned(not(file.content().hash()));
  }

  @Test
  public void files_with_same_path_and_content_are_equal() throws Exception {
    when(createFile(objectsDb, path, string));
    thenReturned(createFile(objectsDb, path, string));
  }

  @Test
  public void files_with_same_path_and_different_content_are_not_equal() throws Exception {
    when(createFile(objectsDb, path, string));
    thenReturned(not(createFile(objectsDb, path, otherString)));
  }

  @Test
  public void files_with_different_paths_and_same_content_are_not_equal() throws Exception {
    when(createFile(objectsDb, path, string));
    thenReturned(not(createFile(objectsDb, otherPath, string)));
  }

  @Test
  public void files_with_different_paths_and_content_are_not_equal() throws Exception {
    when(createFile(objectsDb, path, string));
    thenReturned(not(createFile(objectsDb, otherPath, otherString)));
  }

  @Test
  public void files_with_same_path_and_content_have_equal_hashes() throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, path, string));
    when(file.hash());
    thenReturned(file2.hash());
  }

  @Test
  public void files_with_same_path_and_different_content_have_different_hashes() throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, path, otherString));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_and_same_content_have_different_hashes() throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, otherPath, string));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_and_content_have_different_hashes() throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, otherPath, otherString));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_same_path_and_content_have_equal_hash_codes() throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, path, string));
    when(file.hashCode());
    thenReturned(file2.hashCode());
  }

  @Test
  public void files_with_same_path_and_different_content_have_different_hash_codes()
      throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, path, otherString));
    when(file.hashCode());
    thenReturned(not(file2.hashCode()));
  }

  @Test
  public void files_with_different_paths_and_same_content_have_different_hash_codes()
      throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, otherPath, string));
    when(file.hashCode());
    thenReturned(not(file2.hashCode()));
  }

  @Test
  public void files_with_different_paths_and_content_have_different_hash_codes() throws Exception {
    given(file = createFile(objectsDb, path, string));
    given(file2 = createFile(objectsDb, otherPath, otherString));
    when(file.hashCode());
    thenReturned(not(file2.hashCode()));
  }

  @Test
  public void file_can_be_read_by_hash() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(objectsDb.read(FILE, file.hash()));
    thenReturned(file);
  }

  @Test
  public void file_read_by_hash_has_same_content() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(objectsDb.read(FILE, file.hash()).content());
    thenReturned(file.content());
  }

  @Test
  public void file_read_by_hash_has_same_path() throws Exception {
    given(file = createFile(objectsDb, path, string));
    when(objectsDb.read(FILE, file.hash()).path());
    thenReturned(file.path());
  }

  private static SFile createFile(ObjectsDb objectsDb, Path path, String content) throws Exception {
    FileBuilder fileBuilder = objectsDb.fileBuilder();
    fileBuilder.setPath(path);
    fileBuilder.setContent(createBlob(objectsDb, content));
    return fileBuilder.build();
  }

  private static SBlob createBlob(ObjectsDb objectsDb, String content) throws Exception {
    BlobBuilder blobBuilder = objectsDb.blobBuilder();
    writeAndClose(blobBuilder.openOutputStream(), content);
    return blobBuilder.build();
  }
}
