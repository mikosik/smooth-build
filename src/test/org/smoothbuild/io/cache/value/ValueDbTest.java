package org.smoothbuild.io.cache.value;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.err.NoObjectWithGivenHashError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;
import org.smoothbuild.lang.function.value.StringSet;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

public class ValueDbTest {
  ValueDb valueDb = new ValueDb(new HashedDb(new FakeFileSystem()));

  byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6 };
  byte[] bytes2 = new byte[] { 1, 2, 3, 4, 5, 6, 7 };

  FileSet fileSet;
  FileSet fileSet2;

  File file;
  File file2;
  Path path = path("my/path1");
  Path path2 = path("my/path2");

  StringSet stringSet;
  StringSet stringSet2;

  StringValue stringValue;
  StringValue stringValue2;
  StringValue stringValueRead;
  String string = "a string";
  String string2 = "a string 2";

  BlobObject blob;
  BlobObject blobRead;

  // file vs blob

  @Test
  public void file_hash_is_different_from_file_content_hash() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(blob = valueDb.blob(bytes));
    when(file.hash());
    thenReturned(not(blob.hash()));
  }

  // file set object

  @Test
  public void created_file_set_with_one_file_added_contains_one_file() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    when(Iterables.size(fileSet));
    thenReturned(1);
  }

  @Test
  public void created_file_set_contains_file_with_path_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    when(fileSet.iterator().next().path());
    thenReturned(path);
  }

  @Test
  public void created_file_set_contains_file_with_content_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    when(inputStreamToBytes(fileSet.iterator().next().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_set_with_one_file_added_when_queried_by_hash_contains_one_file()
      throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    when(Iterables.size(valueDb.fileSet(fileSet.hash())));
    thenReturned(1);
  }

  @Test
  public void created_file_set_when_queried_by_hash_contains_file_with_path_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    when(valueDb.fileSet(fileSet.hash()).iterator().next().path());
    thenReturned(path);
  }

  @Test
  public void created_file_set_when_queried_by_hash_contains_file_with_content_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    when(inputStreamToBytes(valueDb.fileSet(fileSet.hash()).iterator().next().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void file_set_with_one_element_has_different_hash_from_that_file() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));

    when(file.hash());
    thenReturned(not(equalTo(fileSet.hash())));
  }

  @Test
  public void file_set_with_one_element_has_different_hash_from_file_set_with_two_elements()
      throws Exception {
    given(file = valueDb.file(path, bytes));
    given(file2 = valueDb.file(path2, bytes2));
    given(fileSet = valueDb.fileSet(ImmutableList.<File> of(file)));
    given(fileSet2 = valueDb.fileSet(ImmutableList.<File> of(file, file2)));

    when(fileSet.hash());
    thenReturned(not(equalTo(fileSet2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_file_set_fails() throws Exception {
    given(fileSet = valueDb.fileSet(HashCode.fromInt(33)));
    when(fileSet).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // string set object

  @Test
  public void created_string_set_with_one_string_added_contains_one_string() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue)));
    when(Iterables.size(stringSet));
    thenReturned(1);
  }

  @Test
  public void created_string_set_contains_string_that_was_added_to_it() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue)));
    when(stringSet.iterator().next().value());
    thenReturned(string);
  }

  @Test
  public void created_string_set_with_one_string_added_when_queried_by_hash_contains_one_string()
      throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue)));
    when(Iterables.size(valueDb.stringSet(stringSet.hash())));
    thenReturned(1);
  }

  @Test
  public void created_string_set_when_queried_by_hash_contains_string_that_was_added_to_it()
      throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue)));
    when(valueDb.stringSet(stringSet.hash()).iterator().next().value());
    thenReturned(string);
  }

  @Test
  public void string_set_with_one_element_has_different_hash_from_that_string() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue)));

    when(stringValue.hash());
    thenReturned(not(equalTo(stringSet.hash())));
  }

  @Test
  public void string_set_with_one_element_has_different_hash_from_string_set_with_two_elements()
      throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringValue2 = valueDb.string(string2));
    given(stringSet = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue)));
    given(stringSet2 = valueDb.stringSet(ImmutableList.<StringValue> of(stringValue,
        stringValue2)));

    when(stringSet.hash());
    thenReturned(not(equalTo(stringSet2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_string_set_fails() throws Exception {
    given(stringSet = valueDb.stringSet(HashCode.fromInt(33)));
    when(stringSet).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // file object

  @Test
  public void created_file_contains_stored_bytes() throws IOException {
    given(file = valueDb.file(path, bytes));
    when(inputStreamToBytes(file.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_contains_stored_path() throws IOException {
    given(file = valueDb.file(path, bytes));
    when(file.path());
    thenReturned(path);
  }

  @Test
  public void files_with_different_bytes_have_different_hashes() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(file2 = valueDb.file(path, bytes2));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_have_different_hashes() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(file2 = valueDb.file(path2, bytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void file_retrieved_via_hash_contains_this_hash() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(file2 = valueDb.file(file.hash()));
    when(file2.hash());
    thenReturned(file.hash());
  }

  @Test
  public void file_retrieved_via_hash_contains_path_that_were_stored() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(file2 = valueDb.file(file.hash()));
    when(file2.path());
    thenReturned(path);
  }

  @Test
  public void file_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(file2 = valueDb.file(file.hash()));
    when(inputStreamToBytes(file2.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    when(valueDb).file(HashCode.fromInt(33));
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // string object

  @Test
  public void created_string_object_contains_stored_string() throws IOException {
    given(stringValue = valueDb.string(string));
    when(stringValue.value());
    thenReturned(string);
  }

  @Test
  public void created_string_contains_correct_hash() throws Exception {
    given(stringValue = valueDb.string(string));
    when(stringValue.hash());
    thenReturned(Hash.string(string));
  }

  @Test
  public void string_retrieved_via_hash_contains_this_hash() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringValueRead = valueDb.string(stringValue.hash()));
    when(stringValueRead.value());
    thenReturned(string);
  }

  @Test
  public void string_object_retrieved_via_hash_contains_string_that_was_stored() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringValueRead = valueDb.string(stringValue.hash()));
    when(stringValueRead.value());
    thenReturned(string);
  }

  @Test
  public void reading_not_stored_string_object_fails() throws Exception {
    given(stringValue = valueDb.string(HashCode.fromInt(33)));
    when(stringValue).value();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // blob object

  @Test
  public void created_blob_contains_stored_bytes() throws IOException {
    given(blob = valueDb.blob(bytes));
    when(inputStreamToBytes(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_blob_contains_correct_hash() throws Exception {
    given(blob = valueDb.blob(bytes));
    when(blob.hash());
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void blob_retrieved_via_hash_contains_this_hash() throws Exception {
    given(blob = valueDb.blob(bytes));
    given(blobRead = valueDb.blob(blob.hash()));
    when(blobRead.hash());
    thenReturned(blob.hash());
  }

  @Test
  public void blob_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(blob = valueDb.blob(bytes));
    given(blobRead = valueDb.blob(blob.hash()));
    when(inputStreamToBytes(blobRead.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(blob = valueDb.blob(HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }
}
