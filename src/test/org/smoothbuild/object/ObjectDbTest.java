package org.smoothbuild.object;

import static org.hamcrest.Matchers.equalTo;
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
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

public class ObjectDbTest {
  ObjectDb objectDb = new ObjectDb(new HashedDb(new FakeFileSystem()));

  byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6 };
  byte[] bytes2 = new byte[] { 1, 2, 3, 4, 5, 6, 7 };

  FileSetObject fileSet;
  FileSetObject fileSet2;

  FileObject file;
  FileObject file2;
  Path path = path("my/path1");
  Path path2 = path("my/path2");

  StringSet stringSet;
  StringSet stringSet2;

  StringObject stringObject;
  StringObject stringObject2;
  StringObject stringObjectRead;
  String string = "a string";
  String string2 = "a string 2";

  BlobObject blob;
  BlobObject blobRead;

  // file vs blob

  @Test
  public void file_hash_is_different_from_file_content_hash() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(blob = objectDb.blob(bytes));
    when(file.hash());
    thenReturned(not(blob.hash()));
  }

  // file set object

  @Test
  public void created_file_set_with_one_file_added_contains_one_file() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    when(Iterables.size(fileSet));
    thenReturned(1);
  }

  @Test
  public void created_file_set_contains_file_with_path_of_file_that_was_added_to_it()
      throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    when(fileSet.iterator().next().path());
    thenReturned(path);
  }

  @Test
  public void created_file_set_contains_file_with_content_of_file_that_was_added_to_it()
      throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    when(inputStreamToBytes(fileSet.iterator().next().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_set_with_one_file_added_when_queried_by_hash_contains_one_file()
      throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    when(Iterables.size(objectDb.fileSet(fileSet.hash())));
    thenReturned(1);
  }

  @Test
  public void created_file_set_when_queried_by_hash_contains_file_with_path_of_file_that_was_added_to_it()
      throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    when(objectDb.fileSet(fileSet.hash()).iterator().next().path());
    thenReturned(path);
  }

  @Test
  public void created_file_set_when_queried_by_hash_contains_file_with_content_of_file_that_was_added_to_it()
      throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    when(inputStreamToBytes(objectDb.fileSet(fileSet.hash()).iterator().next().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void file_set_with_one_element_has_different_hash_from_that_file() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));

    when(file.hash());
    thenReturned(not(equalTo(fileSet.hash())));
  }

  @Test
  public void file_set_with_one_element_has_different_hash_from_file_set_with_two_elements()
      throws Exception {
    given(file = objectDb.file(path, bytes));
    given(file2 = objectDb.file(path2, bytes2));
    given(fileSet = objectDb.fileSet(ImmutableList.<File> of(file)));
    given(fileSet2 = objectDb.fileSet(ImmutableList.<File> of(file, file2)));

    when(fileSet.hash());
    thenReturned(not(equalTo(fileSet2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_file_set_fails() throws Exception {
    given(fileSet = objectDb.fileSet(HashCode.fromInt(33)));
    when(fileSet).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // string set object

  @Test
  public void created_string_set_with_one_string_added_contains_one_string() throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringSet = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject)));
    when(Iterables.size(stringSet));
    thenReturned(1);
  }

  @Test
  public void created_string_set_contains_string_that_was_added_to_it() throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringSet = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject)));
    when(stringSet.iterator().next().value());
    thenReturned(string);
  }

  @Test
  public void created_string_set_with_one_string_added_when_queried_by_hash_contains_one_string()
      throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringSet = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject)));
    when(Iterables.size(objectDb.stringSet(stringSet.hash())));
    thenReturned(1);
  }

  @Test
  public void created_string_set_when_queried_by_hash_contains_string_that_was_added_to_it()
      throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringSet = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject)));
    when(objectDb.stringSet(stringSet.hash()).iterator().next().value());
    thenReturned(string);
  }

  @Test
  public void string_set_with_one_element_has_different_hash_from_that_string() throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringSet = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject)));

    when(stringObject.hash());
    thenReturned(not(equalTo(stringSet.hash())));
  }

  @Test
  public void string_set_with_one_element_has_different_hash_from_string_set_with_two_elements()
      throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringObject2 = objectDb.string(string2));
    given(stringSet = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject)));
    given(stringSet2 = objectDb.stringSet(ImmutableList.<StringValue> of(stringObject,
        stringObject2)));

    when(stringSet.hash());
    thenReturned(not(equalTo(stringSet2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_string_set_fails() throws Exception {
    given(stringSet = objectDb.stringSet(HashCode.fromInt(33)));
    when(stringSet).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // file object

  @Test
  public void created_file_contains_stored_bytes() throws IOException {
    given(file = objectDb.file(path, bytes));
    when(inputStreamToBytes(file.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_contains_stored_path() throws IOException {
    given(file = objectDb.file(path, bytes));
    when(file.path());
    thenReturned(path);
  }

  @Test
  public void files_with_different_bytes_have_different_hashes() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(file2 = objectDb.file(path, bytes2));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_have_different_hashes() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(file2 = objectDb.file(path2, bytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void file_retrieved_via_hash_contains_this_hash() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(file2 = objectDb.file(file.hash()));
    when(file2.hash());
    thenReturned(file.hash());
  }

  @Test
  public void file_retrieved_via_hash_contains_path_that_were_stored() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(file2 = objectDb.file(file.hash()));
    when(file2.path());
    thenReturned(path);
  }

  @Test
  public void file_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(file2 = objectDb.file(file.hash()));
    when(inputStreamToBytes(file2.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    when(objectDb).file(HashCode.fromInt(33));
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // string object

  @Test
  public void created_string_object_contains_stored_string() throws IOException {
    given(stringObject = objectDb.string(string));
    when(stringObject.value());
    thenReturned(string);
  }

  @Test
  public void created_string_contains_correct_hash() throws Exception {
    given(stringObject = objectDb.string(string));
    when(stringObject.hash());
    thenReturned(Hash.string(string));
  }

  @Test
  public void string_retrieved_via_hash_contains_this_hash() throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringObjectRead = objectDb.string(stringObject.hash()));
    when(stringObjectRead.value());
    thenReturned(string);
  }

  @Test
  public void string_object_retrieved_via_hash_contains_string_that_was_stored() throws Exception {
    given(stringObject = objectDb.string(string));
    given(stringObjectRead = objectDb.string(stringObject.hash()));
    when(stringObjectRead.value());
    thenReturned(string);
  }

  @Test
  public void reading_not_stored_string_object_fails() throws Exception {
    given(stringObject = objectDb.string(HashCode.fromInt(33)));
    when(stringObject).value();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // blob object

  @Test
  public void created_blob_contains_stored_bytes() throws IOException {
    given(blob = objectDb.blob(bytes));
    when(inputStreamToBytes(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_blob_contains_correct_hash() throws Exception {
    given(blob = objectDb.blob(bytes));
    when(blob.hash());
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void blob_retrieved_via_hash_contains_this_hash() throws Exception {
    given(blob = objectDb.blob(bytes));
    given(blobRead = objectDb.blob(blob.hash()));
    when(blobRead.hash());
    thenReturned(blob.hash());
  }

  @Test
  public void blob_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(blob = objectDb.blob(bytes));
    given(blobRead = objectDb.blob(blob.hash()));
    when(inputStreamToBytes(blobRead.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(blob = objectDb.blob(HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }
}
