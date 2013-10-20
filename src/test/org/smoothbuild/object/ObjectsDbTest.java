package org.smoothbuild.object;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

import com.google.common.hash.HashCode;

public class ObjectsDbTest {
  ObjectsDb objectsDb = new ObjectsDb(new HashedDb(new FakeFileSystem()));

  byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6 };
  byte[] bytes2 = new byte[] { 1, 2, 3, 4, 5, 6, 7 };

  BlobObject blob;
  BlobObject blobRead;

  FileObject file;
  FileObject file2;
  Path path1 = path("my/path1");
  Path path2 = path("my/path2");

  // file vs blob

  @Test
  public void file_hash_is_different_from_file_content_hash() throws Exception {
    given(file = objectsDb.file(path1, bytes));
    given(blob = objectsDb.blob(bytes));
    when(file.hash());
    thenReturned(not(blob.hash()));
  }

  // file object

  @Test
  public void created_file_contains_stored_bytes() throws IOException {
    given(file = objectsDb.file(path1, bytes));
    when(inputStreamToBytes(file.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_contains_stored_path() throws IOException {
    given(file = objectsDb.file(path1, bytes));
    when(file.path());
    thenReturned(path1);
  }

  @Test
  public void files_with_different_bytes_have_different_hashes() throws Exception {
    given(file = objectsDb.file(path1, bytes));
    given(file2 = objectsDb.file(path1, bytes2));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_have_different_hashes() throws Exception {
    given(file = objectsDb.file(path1, bytes));
    given(file2 = objectsDb.file(path2, bytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void file_retrieved_via_hash_contains_this_hash() throws Exception {
    given(file = objectsDb.file(path1, bytes));
    given(file2 = objectsDb.file(file.hash()));
    when(file2.hash());
    thenReturned(file.hash());
  }

  @Test
  public void file_retrieved_via_hash_contains_path_that_were_stored() throws Exception {
    given(file = objectsDb.file(path1, bytes));
    given(file2 = objectsDb.file(file.hash()));
    when(file2.path());
    thenReturned(path1);
  }

  @Test
  public void file_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(file = objectsDb.file(path1, bytes));
    given(file2 = objectsDb.file(file.hash()));
    when(inputStreamToBytes(file2.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    when(objectsDb).file(HashCode.fromInt(33));
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // blob object

  @Test
  public void created_blob_contains_stored_bytes() throws IOException {
    given(blob = objectsDb.blob(bytes));
    when(inputStreamToBytes(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_blob_contains_correct_hash() throws Exception {
    given(blob = objectsDb.blob(bytes));
    when(blob.hash());
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void blob_retrieved_via_hash_contains_this_hash() throws Exception {
    given(blob = objectsDb.blob(bytes));
    given(blobRead = objectsDb.blob(blob.hash()));
    when(blobRead.hash());
    thenReturned(blob.hash());
  }

  @Test
  public void blob_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(blob = objectsDb.blob(bytes));
    given(blobRead = objectsDb.blob(blob.hash()));
    when(inputStreamToBytes(blobRead.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(blob = objectsDb.blob(HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }
}
