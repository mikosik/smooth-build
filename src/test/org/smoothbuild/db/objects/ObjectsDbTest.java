package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;

public class ObjectsDbTest {
  byte[] bytes = new byte[] { 1, 2, 3, 4 };
  byte[] bytes2 = new byte[] { 1, 2, 3, 4, 5 };
  byte[] bytes3 = new byte[] { 1, 2, 3, 4, 5, 6 };
  byte[] bytes4 = new byte[] { 1, 2, 3, 4, 5, 6, 7 };

  SArray<SFile> fileArray;
  SArray<SFile> fileArray2;

  SFile file;
  SFile file2;
  SFile file3;
  SFile file4;
  Path path = path("my/path1");
  Path path2 = path("my/path2");
  Path path3 = path("my/path3");
  Path path4 = path("my/path4");

  SArray<SBlob> blobArray;
  SArray<SBlob> blobArray2;

  SBlob blob;
  SBlob blob2;
  SBlob blob3;
  SBlob blob4;
  SBlob blobRead;

  SArray<SString> stringArray;
  SArray<SString> stringArray2;

  SString stringValue;
  SString stringValue2;
  SString stringValue3;
  SString stringValue4;
  SString stringValueRead;
  String string = "a string";
  String string2 = "a string 2";
  String string3 = "a string 3";
  String string4 = "a string 4";

  ObjectsDb objectsDb = new ObjectsDb(new HashedDb(new FakeFileSystem()));

  // file vs blob

  @Test
  public void file_hash_is_different_from_file_content_hash() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    when(file.hash());
    thenReturned(not(blob.hash()));
  }

  // empty array

  @Test
  public void created_empty_array_is_empty() throws Exception {
    when(objectsDb.arrayBuilder(EMPTY_ARRAY).build());
    thenReturned(Matchers.emptyIterable());
  }

  // file array

  @Test
  public void created_empty_file_array_is_empty() throws Exception {
    given(fileArray = objectsDb.arrayBuilder(FILE_ARRAY).build());
    then(fileArray, Matchers.emptyIterable());
  }

  @Test
  public void created_file_array_contains_file_that_was_added_to_it() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(fileArray = objectsDb.arrayBuilder(FILE_ARRAY).add(file).build());
    then(fileArray, contains(new FakeFile(path, bytes)));
  }

  @Test
  public void created_file_array_contains_all_files_that_were_added_to_it() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blob2 = writeBlob(objectsDb, bytes2));
    given(blob3 = writeBlob(objectsDb, bytes3));
    given(blob4 = writeBlob(objectsDb, bytes4));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(file2 = objectsDb.fileBuilder().setPath(path2).setContent(blob2).build());
    given(file3 = objectsDb.fileBuilder().setPath(path3).setContent(blob3).build());
    given(file4 = objectsDb.fileBuilder().setPath(path4).setContent(blob4).build());

    given(fileArray =
        objectsDb.arrayBuilder(FILE_ARRAY).add(file).add(file2).add(file3).add(file4).build());
    then(fileArray, contains(new FakeFile(path, bytes), new FakeFile(path2, bytes2), new FakeFile(
        path3, bytes3), new FakeFile(path4, bytes4)));
  }

  @Test
  public void created_file_array_with_one_file_added_when_queried_by_hash_contains_that_file()
      throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(fileArray = objectsDb.arrayBuilder(FILE_ARRAY).add(file).build());
    then(objectsDb.read(FILE_ARRAY, fileArray.hash()), contains(new FakeFile(path, bytes)));
  }

  @Test
  public void file_array_with_one_file_has_different_hash_from_that_file() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(fileArray = objectsDb.arrayBuilder(FILE_ARRAY).add(file).build());

    when(file.hash());
    thenReturned(not(equalTo(fileArray.hash())));
  }

  @Test
  public void file_array_with_one_element_has_different_hash_from_file_array_with_two_elements()
      throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blob2 = writeBlob(objectsDb, bytes2));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(file2 = objectsDb.fileBuilder().setPath(path2).setContent(blob2).build());
    given(fileArray = objectsDb.arrayBuilder(FILE_ARRAY).add(file).build());
    given(fileArray2 = objectsDb.arrayBuilder(FILE_ARRAY).add(file).add(file2).build());

    when(fileArray.hash());
    thenReturned(not(equalTo(fileArray2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(fileArray = objectsDb.read(FILE_ARRAY, HashCode.fromInt(33)));
    when(fileArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  // blob array

  @Test
  public void created_empty_blob_array_is_empty() throws Exception {
    given(blobArray = objectsDb.arrayBuilder(BLOB_ARRAY).build());
    then(blobArray, Matchers.emptyIterable());
  }

  @Test
  public void created_blob_array_with_one_blob_added_contains_that_blob() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    when(blobArray = objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    then(blobArray, contains(new FakeBlob(bytes)));
  }

  @Test
  public void created_blob_array_with_blobs_added_contains_all_blobs() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blob2 = writeBlob(objectsDb, bytes2));
    given(blob3 = writeBlob(objectsDb, bytes3));
    given(blob4 = writeBlob(objectsDb, bytes4));
    when(blobArray =
        objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).add(blob2).add(blob3).add(blob4).build());
    then(blobArray, contains(new FakeBlob(bytes), new FakeBlob(bytes2), new FakeBlob(bytes3),
        new FakeBlob(bytes4)));
  }

  @Test
  public void created_blob_array_with_one_blob_added_when_queried_by_hash_contains_that_blob()
      throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    when(blobArray = objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    then(objectsDb.read(BLOB_ARRAY, blobArray.hash()), contains(new FakeBlob(bytes)));
  }

  @Test
  public void blob_array_with_one_blob_has_different_hash_from_that_blob() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blobArray = objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    when(blob.hash());
    thenReturned(not(equalTo(blobArray.hash())));
  }

  @Test
  public void blob_array_with_one_element_has_different_hash_from_blob_array_with_two_elements()
      throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blob2 = writeBlob(objectsDb, bytes2));
    given(blobArray = objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    given(blobArray2 = objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).add(blob2).build());

    when(blobArray.hash());
    thenReturned(not(equalTo(blobArray2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(blobArray = objectsDb.read(BLOB_ARRAY, HashCode.fromInt(33)));
    when(blobArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  // string array

  @Test
  public void created_empty_string_array_is_empty() throws Exception {
    given(stringArray = objectsDb.arrayBuilder(STRING_ARRAY).build());
    then(stringArray, Matchers.emptyIterable());
  }

  @Test
  public void created_string_array_with_one_string_added_contains_that_string() throws Exception {
    given(stringValue = objectsDb.string(string));
    when(stringArray = objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    then(stringArray, contains(new FakeString(string)));
  }

  @Test
  public void created_string_array_with_strings_added_contains_all_strings() throws Exception {
    given(stringValue = objectsDb.string(string));
    given(stringValue2 = objectsDb.string(string2));
    given(stringValue3 = objectsDb.string(string3));
    given(stringValue4 = objectsDb.string(string4));
    when(stringArray =
        objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).add(stringValue2).add(stringValue3)
            .add(stringValue4).build());
    then(stringArray, contains(new FakeString(string), new FakeString(string2), new FakeString(
        string3), new FakeString(string4)));
  }

  @Test
  public void created_string_array_with_one_string_added_when_queried_by_hash_contains_that_string()
      throws Exception {
    given(stringValue = objectsDb.string(string));
    when(stringArray = objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    then(objectsDb.read(STRING_ARRAY, stringArray.hash()), contains(new FakeString(string)));
  }

  @Test
  public void string_array_with_one_string_has_different_hash_from_that_string() throws Exception {
    given(stringValue = objectsDb.string(string));
    given(stringArray = objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());

    when(stringValue.hash());
    thenReturned(not(equalTo(stringArray.hash())));
  }

  @Test
  public void string_array_with_one_element_has_different_hash_from_string_array_with_two_elements()
      throws Exception {
    given(stringValue = objectsDb.string(string));
    given(stringValue2 = objectsDb.string(string2));
    given(stringArray = objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    given(stringArray2 =
        objectsDb.arrayBuilder(STRING_ARRAY).add(stringValue).add(stringValue2).build());

    when(stringArray.hash());
    thenReturned(not(equalTo(stringArray2.hash())));
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(stringArray = objectsDb.read(STRING_ARRAY, HashCode.fromInt(33)));
    when(stringArray).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  // file object

  @Test
  public void created_file_contains_stored_bytes() throws IOException {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    when(inputStreamToBytes(file.content().openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_file_contains_stored_path() throws IOException {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    when(file.path());
    thenReturned(path);
  }

  @Test
  public void files_with_different_bytes_have_different_hashes() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blob2 = writeBlob(objectsDb, bytes2));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(file2 = objectsDb.fileBuilder().setPath(path).setContent(blob2).build());
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void files_with_different_paths_have_different_hashes() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(file2 = objectsDb.fileBuilder().setPath(path2).setContent(blob).build());
    when(file.hash());
    thenReturned(not(file2.hash()));
  }

  @Test
  public void file_retrieved_via_hash_contains_this_hash() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(file2 = objectsDb.read(FILE, file.hash()));
    when(file2.hash());
    thenReturned(file.hash());
  }

  @Test
  public void file_retrieved_via_hash_contains_path_that_were_stored() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    given(file2 = objectsDb.read(FILE, file.hash()));
    when(file2.path());
    thenReturned(path);
  }

  @Test
  public void file_retrieved_via_hash_contains_blob_that_were_stored() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(file = objectsDb.fileBuilder().setPath(path).setContent(blob).build());
    when(objectsDb.read(FILE, file.hash())).content();
    thenReturned(blob);
  }

  @Test
  public void reading_not_stored_file_fails() throws Exception {
    when(objectsDb).read(FILE, HashCode.fromInt(33));
    thenThrown(NoObjectWithGivenHashError.class);
  }

  // blob object

  @Test
  public void created_blob_contains_stored_bytes() throws IOException {
    given(blob = writeBlob(objectsDb, bytes));
    when(inputStreamToBytes(blob.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void created_blob_contains_correct_hash() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    when(blob.hash());
    thenReturned(Hash.bytes(bytes));
  }

  @Test
  public void blob_retrieved_via_hash_contains_this_hash() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blobRead = objectsDb.read(BLOB, blob.hash()));
    when(blobRead.hash());
    thenReturned(blob.hash());
  }

  @Test
  public void blob_retrieved_via_hash_contains_bytes_that_were_stored() throws Exception {
    given(blob = writeBlob(objectsDb, bytes));
    given(blobRead = objectsDb.read(BLOB, blob.hash()));
    when(inputStreamToBytes(blobRead.openInputStream()));
    thenReturned(bytes);
  }

  @Test
  public void reading_not_stored_blob_fails() throws Exception {
    given(blob = objectsDb.read(BLOB, HashCode.fromInt(33)));
    when(blob).openInputStream();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  // string object

  @Test
  public void created_string_object_contains_stored_string() throws IOException {
    given(stringValue = objectsDb.string(string));
    when(stringValue.value());
    thenReturned(string);
  }

  @Test
  public void created_string_contains_correct_hash() throws Exception {
    given(stringValue = objectsDb.string(string));
    when(stringValue.hash());
    thenReturned(Hash.string(string));
  }

  @Test
  public void string_retrieved_via_hash_contains_this_hash() throws Exception {
    given(stringValue = objectsDb.string(string));
    given(stringValueRead = objectsDb.read(STRING, stringValue.hash()));
    when(stringValueRead.value());
    thenReturned(string);
  }

  @Test
  public void string_object_retrieved_via_hash_contains_string_that_was_stored() throws Exception {
    given(stringValue = objectsDb.string(string));
    given(stringValueRead = objectsDb.read(STRING, stringValue.hash()));
    when(stringValueRead.value());
    thenReturned(string);
  }

  @Test
  public void reading_not_stored_string_object_fails() throws Exception {
    given(stringValue = objectsDb.read(STRING, HashCode.fromInt(33)));
    when(stringValue).value();
    thenThrown(NoObjectWithGivenHashError.class);
  }

  private static SBlob writeBlob(ObjectsDb objectsDb, byte[] bytes) throws IOException {
    BlobBuilder builder = objectsDb.blobBuilder();
    Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    return builder.build();
  }
}
