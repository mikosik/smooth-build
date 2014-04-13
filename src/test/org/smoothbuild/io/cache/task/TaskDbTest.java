package org.smoothbuild.io.cache.task;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.err.NoObjectWithGivenHashError;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskDbTest {
  HashedDb hashedDb = new HashedDb(new FakeFileSystem());
  ValueDb valueDb = new ValueDb(new HashedDb(new FakeFileSystem()));
  TaskDb taskDb = new TaskDb(hashedDb, valueDb);
  HashCode hash = Hash.string("abc");

  byte[] bytes = new byte[] {};
  Path path = path("file/path");

  Message message;
  SArray<SFile> fileArray;
  SArray<SBlob> blobArray;
  SArray<SString> stringArray;
  SFile file;
  SBlob blob;
  SString stringValue;
  String string = "some string";

  @Test
  public void result_cache_does_not_contain_not_stored_result() {
    when(taskDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_stored_result() {
    given(taskDb).store(hash, new TaskResult<>(new FakeString("result"), Empty.messageList()));
    when(taskDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    when(taskDb).read(hash, STRING);
    thenThrown(NoObjectWithGivenHashError.class);
  }

  @Test
  public void stored_messages_can_be_read_back() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(message = new Message(ERROR, "message string"));
    given(taskDb).store(hash, new TaskResult<>(blob, ImmutableList.of(message)));
    when(taskDb.read(hash, BLOB).messages());
    thenReturned(contains(message));
  }

  @Test
  public void stored_file_array_can_be_read_back() throws Exception {
    given(file = valueDb.writeFile(path, new FakeBlob(bytes)));
    given(fileArray = valueDb.arrayBuilder(FILE_ARRAY).add(file).build());
    given(taskDb).store(hash, new TaskResult<>(fileArray, Empty.messageList()));
    when(taskDb.read(hash, FILE_ARRAY).value().iterator().next());
    thenReturned(file);
  }

  @Test
  public void stored_blob_array_can_be_read_back() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(blobArray = valueDb.arrayBuilder(BLOB_ARRAY).add(blob).build());
    given(taskDb).store(hash, new TaskResult<>(blobArray, Empty.messageList()));
    when(taskDb.read(hash, BLOB_ARRAY).value().iterator().next());
    thenReturned(blob);
  }

  @Test
  public void stored_string_array_can_be_read_back() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(stringArray = valueDb.arrayBuilder(STRING_ARRAY).add(stringValue).build());
    given(taskDb).store(hash, new TaskResult<>(stringArray, Empty.messageList()));
    when(taskDb.read(hash, STRING_ARRAY).value().iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void stored_file_can_be_read_back() throws Exception {
    given(file = valueDb.writeFile(path, new FakeBlob(bytes)));
    given(taskDb).store(hash, new TaskResult<>(file, Empty.messageList()));
    when(taskDb.read(hash, FILE).value());
    thenReturned(file);
  }

  @Test
  public void stored_blob_can_be_read_back() throws Exception {
    given(blob = valueDb.writeBlob(bytes));
    given(taskDb).store(hash, new TaskResult<>(blob, Empty.messageList()));
    when(taskDb.read(hash, BLOB).value());
    thenReturned(blob);
  }

  @Test
  public void stored_string_can_be_read_back() throws Exception {
    given(stringValue = valueDb.writeString(string));
    given(taskDb).store(hash, new TaskResult<>(stringValue, Empty.messageList()));
    when(taskDb.read(hash, STRING).value().value());
    thenReturned(string);
  }
}
