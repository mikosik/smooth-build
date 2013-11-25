package org.smoothbuild.io.cache.value;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
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
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;

public class ValueDbTest {
  byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6 };
  byte[] bytes2 = new byte[] { 1, 2, 3, 4, 5, 6, 7 };

  SArray<SFile> fileArray;
  SArray<SFile> fileArray2;

  SFile file;
  SFile file2;
  Path path = path("my/path1");
  Path path2 = path("my/path2");

  SArray<SBlob> blobArray;
  SArray<SBlob> blobArray2;

  SBlob blob;
  SBlob blob2;
  SBlob blobRead;

  SArray<SString> stringArray;
  SArray<SString> stringArray2;

  SString stringValue;
  SString stringValue2;
  SString stringValueRead;
  String string = "a string";
  String string2 = "a string 2";

  ValueDb valueDb = new ValueDb(new HashedDb(new FakeFileSystem()));

  // file vs blob

  @Test
  public void file_hash_is_different_from_file_content_hash() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(blob = valueDb.writeBlob(bytes));
    when(file.hash());
    thenReturned(not(blob.hash()));
  }

  // file array

  @Test
  public void created_file_array_with_one_file_added_contains_one_file() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    when(Iterables.size(fileArray));
    thenReturned(1);
  }

  @Test
  public void created_file_array_contains_file_with_path_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    when(fileArray.iterator().next().path());
    thenReturned(path);
  }

  @Test
  public void created_file_array_contains_file_with_content_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    when(inputStreamToBytes(fileArray.iterator().next().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_array_with_one_file_added_when_queried_by_hash_contains_one_file()
      throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    when(Iterables.size(valueDb.read(FILE_ARRAY, fileArray.hash())));
    thenReturned(1);
  }

  @Test
  public void created_file_array_when_queried_by_hash_contains_file_with_path_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    when(valueDb.read(FILE_ARRAY, fileArray.hash()).iterator().next().path());
    thenReturned(path);
  }

  @Test
  public void created_file_array_when_queried_by_hash_contains_file_with_content_of_file_that_was_added_to_it()
      throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    when(inputStreamToBytes(valueDb.read(FILE_ARRAY, fileArray.hash()).iterator().next()
        .openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void file_array_with_one_element_has_different_hash_from_that_file() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());

    when(file.hash());
    thenReturned(not(equalTo(fileArray.hash())));
  }

  @Test
  public void file_array_with_one_element_has_different_hash_from_file_array_with_two_elements()
      throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(file2 = valueDb.writeFile(path2, bytes2));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    given(fileArray2 = valueDb.arrayBuilder(FILE_ARRAY).add(file).add(file2).build());

    when(fileArray.hash());
    thenReturned(not(equalTo(fileArray2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(fileArray = valueDb.read(FILE_ARRAY, HashCode.fromInt(33)));
    when(fileArray).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // blob array

  @Test
  public void created_blob_array_with_one_blob_added_contains_one_blob() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    when(Iterables.size(blobArray));
    thenReturned(1);
  }

  @Test
  public void created_blob_array_contains_blob_with_content_of_blob_that_was_added_to_it()
      throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    when(inputStreamToBytes(blobArray.iterator().next().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_blob_array_with_one_blob_added_when_queried_by_hash_contains_one_blob()
      throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    when(Iterables.size(valueDb.read(BLOB_ARRAY, blobArray.hash())));
    thenReturned(1);
  }

  @Test
  public void created_blob_array_when_queried_by_hash_contains_blob_with_content_of_blob_that_was_added_to_it()
      throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    when(inputStreamToBytes(valueDb.read(BLOB_ARRAY, blobArray.hash()).iterator().next()
        .openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void blob_array_with_one_element_has_different_hash_from_that_blob() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    when(blob.hash());
    thenReturned(not(equalTo(blobArray.hash())));
  }

  @Test
  public void blob_array_with_one_element_has_different_hash_from_blob_array_with_two_elements()
      throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blob2 = valueDb.writeBlob(bytes2));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    given(blobArray2 = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).add(blob2).build());

    when(blobArray.hash());
    thenReturned(not(equalTo(blobArray2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(blobArray = valueDb.read(BLOB_ARRAY, HashCode.fromInt(33)));
    when(blobArray).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // string array

  @Test
  public void created_string_array_with_one_string_added_contains_one_string() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    when(Iterables.size(stringArray));
    thenReturned(1);
  }

  @Test
  public void created_string_array_contains_string_that_was_added_to_it() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    when(stringArray.iterator().next().value());
    thenReturned(string);
  }

  @Test
  public void created_string_array_with_one_string_added_when_queried_by_hash_contains_one_string()
      throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    when(Iterables.size(valueDb.read(STRING_ARRAY, stringArray.hash())));
    thenReturned(1);
  }

  @Test
  public void created_string_array_when_queried_by_hash_contains_string_that_was_added_to_it()
      throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    when(valueDb.read(STRING_ARRAY, stringArray.hash()).iterator().next().value());
    thenReturned(string);
  }

  @Test
  public void string_array_with_one_element_has_different_hash_from_that_string() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());

    when(stringValue.hash());
    thenReturned(not(equalTo(stringArray.hash())));
  }

  @Test
  public void string_array_with_one_element_has_different_hash_from_string_array_with_two_elements()
      throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringValue2 = valueDb.writeString(string2));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    given(stringArray2 = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).add(stringValue2)
        .build());

    when(stringArray.hash());
    thenReturned(not(equalTo(stringArray2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(stringArray = valueDb.read(STRING_ARRAY, HashCode.fromInt(33)));
    when(stringArray).iterator();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // file object

  @Test
  public void created_file_contains_stored_bytes() throws IOException {
    given(file = valueDb.writeFile(path, bytes));
    when(inputStreamToBytes(file.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_contains_stored_path() throws IOException {
    given(file = valueDb.writeFile(path, bytes));
    when(file.path());
    thenReturned(path);
  }

  @Test
  public void files_with_different_bytes_have_different_hashes() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(file2 = valueDb.writeFile(path, bytes2));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_have_different_hashes() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(file2 = valueDb.writeFile(path2, bytes));
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void file_retrieved_via_hash_contains_this_hash() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(file2 = valueDb.read(FILE, file.hash()));
    when(file2.hash());
    thenReturned(file.hash());
  }

  @Test
  public void file_retrieved_via_hash_contains_path_that_were_stored() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(file2 = valueDb.read(FILE, file.hash()));
    when(file2.path());
    thenReturned(path);
  }

  @Test
  public void file_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(file = valueDb.writeFile(path, bytes));
    given(file2 = valueDb.read(FILE, file.hash()));
    when(inputStreamToBytes(file2.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    when(valueDb).read(FILE, HashCode.fromInt(33));
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // string object

  @Test
  public void created_string_object_contains_stored_string() throws IOException {
    given(stringValue = valueDb.writeString(string));
    when(stringValue.value());
    thenReturned(string);
  }

  @Test
  public void created_string_contains_correct_hash() throws Exception {
    given(stringValue = valueDb.writeString(string));
    when(stringValue.hash());
    thenReturned(Hash.string(string));
  }

  @Test
  public void string_retrieved_via_hash_contains_this_hash() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringValueRead = valueDb.read(STRING, stringValue.hash()));
    when(stringValueRead.value());
    thenReturned(string);
  }

  @Test
  public void string_object_retrieved_via_hash_contains_string_that_was_stored() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringValueRead = valueDb.read(STRING, stringValue.hash()));
    when(stringValueRead.value());
    thenReturned(string);
  }

  @Test
  public void reading_not_stored_string_object_fails() throws Exception {
    given(stringValue = valueDb.read(STRING, HashCode.fromInt(33)));
    when(stringValue).value();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  // blob object

  @Test
  public void created_blob_contains_stored_bytes() throws IOException {
    given(blob = valueDb.writeBlob(bytes));
    when(inputStreamToBytes(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_blob_contains_correct_hash() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    when(blob.hash());
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void blob_retrieved_via_hash_contains_this_hash() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobRead = valueDb.read(BLOB, blob.hash()));
    when(blobRead.hash());
    thenReturned(blob.hash());
  }

  @Test
  public void blob_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobRead = valueDb.read(BLOB, blob.hash()));
    when(inputStreamToBytes(blobRead.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(blob = valueDb.read(BLOB, HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }
}
