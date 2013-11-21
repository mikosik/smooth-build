package org.smoothbuild.io.cache.task;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
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
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskDbTest {
  HashedDb taskResultsDb = new HashedDb(new FakeFileSystem());
  ValueDb valueDb = new ValueDb(new HashedDb(new FakeFileSystem()));
  TaskDb taskDb = new TaskDb(taskResultsDb, valueDb);
  HashCode hash = Hash.string("abc");

  byte[] bytes = new byte[] {};
  Path path = path("file/path");

  Message message;
  Array<File> fileSet;
  Array<Blob> blobSet;
  Array<StringValue> stringSet;
  File file;
  Blob blob;
  StringValue stringValue;
  String string = "some string";

  @Test
  public void result_cache_does_not_contain_not_stored_result() {
    when(taskDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_stored_result() {
    given(taskDb).store(hash, new CachedResult(new FakeString("result"), Empty.messageList()));
    when(taskDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    when(taskDb).read(hash);
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  @Test
  public void stored_messages_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(message = new Message(ERROR, "message string"));
    given(taskDb).store(hash, new CachedResult(file, ImmutableList.of(message)));
    when(taskDb.read(hash).messages());
    thenReturned(contains(message));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void stored_file_set_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileArrayBuilder().add(file).build());
    given(taskDb).store(hash, new CachedResult(fileSet, Empty.messageList()));
    when(((Array<File>) taskDb.read(hash).value()).iterator().next());
    thenReturned(file);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void stored_blob_set_can_be_read_back() throws Exception {
    given(blob = valueDb.blob(bytes));
    given(blobSet = valueDb.blobArrayBuilder().add(blob).build());
    given(taskDb).store(hash, new CachedResult(blobSet, Empty.messageList()));
    when(((Array<Blob>) taskDb.read(hash).value()).iterator().next());
    thenReturned(blob);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void stored_string_set_can_be_read_back() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringArrayBuilder().add(stringValue).build());
    given(taskDb).store(hash, new CachedResult(stringSet, Empty.messageList()));
    when(((Array<StringValue>) taskDb.read(hash).value()).iterator().next());
    thenReturned(stringValue);
  }

  @Test
  public void stored_file_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(taskDb).store(hash, new CachedResult(file, Empty.messageList()));
    when(taskDb.read(hash).value());
    thenReturned(file);
  }

  @Test
  public void stored_blob_can_be_read_back() throws Exception {
    given(blob = valueDb.blob(bytes));
    given(taskDb).store(hash, new CachedResult(blob, Empty.messageList()));
    when(taskDb.read(hash).value());
    thenReturned(blob);
  }

  @Test
  public void stored_string_can_be_read_back() throws Exception {
    given(stringValue = valueDb.string(string));
    given(taskDb).store(hash, new CachedResult(stringValue, Empty.messageList()));
    when(((StringValue) taskDb.read(hash).value()).value());
    thenReturned(string);
  }
}
