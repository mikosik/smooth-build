package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.util.Streams.inputStreamToByteArray;
import static org.smoothbuild.util.Streams.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

import com.google.common.hash.HashCode;

public class FileTest {
  private final byte[] bytes = new byte[] { 1, 2, 3 };
  private final byte[] otherBytes = new byte[] { 4, 5, 6 };
  private final String path = "path";
  private final String otherPath = "other/path";

  private TypeSystem typeSystem;
  private ValuesDb valuesDb;
  private Struct file;
  private Struct file2;
  private HashCode hash;

  @Before
  public void before() {
    HashedDb hashedDb = new HashedDb();
    typeSystem = new TypeSystem(new TypesDb(hashedDb));
    valuesDb = new ValuesDb(hashedDb, typeSystem);
  }

  @Test
  public void null_path_is_forbidden() throws Exception {
    when(valuesDb).file(null, createBlob(valuesDb, bytes));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_content_is_forbidden() throws Exception {
    when(valuesDb).file(valuesDb.string(path), null);
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_of_sfile_is_file() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when(file).type();
    thenReturned(typeSystem.file());
  }

  @Test
  public void path_contains_path_passed_to_builder() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when((SString) file.get("path")).value();
    thenReturned(path);
  }

  @Test
  public void content_contains_data_passed_to_builder() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when(inputStreamToByteArray(((Blob) file.get("content")).openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void file_hash_is_different_of_its_content_hash() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when(file.hash());
    thenReturned(not(((Blob) file.get("content")).hash()));
  }

  @Test
  public void files_with_same_path_and_content_are_equal() throws Exception {
    when(createFile(valuesDb, path, bytes));
    thenReturned(createFile(valuesDb, path, bytes));
  }

  @Test
  public void files_with_same_path_and_different_content_are_not_equal() throws Exception {
    when(createFile(valuesDb, path, bytes));
    thenReturned(not(createFile(valuesDb, path, otherBytes)));
  }

  @Test
  public void files_with_different_paths_and_same_content_are_not_equal() throws Exception {
    when(createFile(valuesDb, path, bytes));
    thenReturned(not(createFile(valuesDb, otherPath, bytes)));
  }

  @Test
  public void files_with_different_paths_and_content_are_not_equal() throws Exception {
    when(createFile(valuesDb, path, bytes));
    thenReturned(not(createFile(valuesDb, otherPath, otherBytes)));
  }

  @Test
  public void files_with_same_path_and_content_have_equal_hashes() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, path, bytes));
    when(file.hash());
    thenReturned(file2.hash());
  }

  @Test
  public void files_with_same_path_and_different_content_have_different_hashes() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, path, otherBytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_and_same_content_have_different_hashes() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, otherPath, bytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_and_content_have_different_hashes() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, otherPath, otherBytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_same_path_and_content_have_equal_hash_codes() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, path, bytes));
    when(file.hashCode());
    thenReturned(file2.hashCode());
  }

  @Test
  public void files_with_same_path_and_different_content_have_different_hash_codes()
      throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, path, otherBytes));
    when(file.hashCode());
    thenReturned(not(file2.hashCode()));
  }

  @Test
  public void files_with_different_paths_and_same_content_have_different_hash_codes()
      throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, otherPath, bytes));
    when(file.hashCode());
    thenReturned(not(file2.hashCode()));
  }

  @Test
  public void files_with_different_paths_and_content_have_different_hash_codes() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    given(file2 = createFile(valuesDb, otherPath, otherBytes));
    when(file.hashCode());
    thenReturned(not(file2.hashCode()));
  }

  @Test
  public void file_can_be_read_by_hash() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when(typeSystem.file().newValue(file.hash()));
    thenReturned(file);
  }

  @Test
  public void file_read_by_hash_has_same_content() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when(typeSystem.file().newValue(file.hash()).get("content"));
    thenReturned(file.get("content"));
  }

  @Test
  public void file_read_by_hash_has_same_path() throws Exception {
    given(file = createFile(valuesDb, path, bytes));
    when((SString) typeSystem.file().newValue(file.hash()).get("path"));
    thenReturned(file.get("path"));
  }

  @Test
  public void to_string_contains_type_name_path_and_bytes_count() throws Exception {
    given(file = createFile(valuesDb, path, new byte[] { 1, 2, 3 }));
    when(file).toString();
    thenReturned("File(content=Blob(3 bytes), path=" + path + ")");
  }

  private static Struct createFile(ValuesDb valuesDb, String path, byte[] content)
      throws Exception {
    return valuesDb.file(valuesDb.string(path), createBlob(valuesDb, content));
  }

  private static Blob createBlob(ValuesDb valuesDb, byte[] content) throws Exception {
    BlobBuilder blobBuilder = valuesDb.blobBuilder();
    writeAndClose(blobBuilder, content);
    return blobBuilder.build();
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    when(() -> typeSystem.file().newValue(hash).get("field"));
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
