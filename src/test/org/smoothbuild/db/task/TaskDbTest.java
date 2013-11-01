package org.smoothbuild.db.task;

import static com.google.common.collect.Lists.newArrayList;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.smoothbuild.testing.plugin.FileMatchers.equalTo;
import static org.smoothbuild.testing.plugin.StringSetMatchers.containsOnly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hash.Hash;
import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.hash.err.NoObjectWithGivenHashError;
import org.smoothbuild.db.value.ValueDb;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.plugin.FakeString;

import com.google.common.hash.HashCode;

public class TaskDbTest {
  HashedDb taskResultsDb = new HashedDb(new FakeFileSystem());
  ValueDb valueDb = new ValueDb(new HashedDb(new FakeFileSystem()));
  TaskDb taskDb = new TaskDb(taskResultsDb, valueDb);
  HashCode hash = Hash.string("abc");

  byte[] bytes = new byte[] {};
  Path path = path("file/path");

  FileSet fileSet;
  StringSet stringSet;
  File file;
  StringValue stringValue;
  String string = "some string";

  @Test
  public void result_cache_does_not_contain_not_stored_result() {
    when(taskDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_stored_result() {
    given(taskDb).store(hash, new FakeString("result"));
    when(taskDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    when(taskDb).read(hash);
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  @Test
  public void stored_file_set_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(newArrayList(file)));
    given(taskDb).store(hash, fileSet);
    when(((FileSet) taskDb.read(hash)).iterator().next());
    thenReturned(equalTo(file));
  }

  @Test
  public void stored_string_set_can_be_read_back() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(newArrayList(stringValue)));
    given(taskDb).store(hash, stringSet);
    when(taskDb.read(hash));
    thenReturned(containsOnly(string));
  }

  @Test
  public void stored_file_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(taskDb).store(hash, file);
    when(taskDb.read(hash));
    thenReturned(equalTo(file));
  }

  @Test
  public void stored_string_object_can_be_read_back() throws Exception {
    given(stringValue = valueDb.string(string));
    given(taskDb).store(hash, stringValue);
    when(((StringValue) taskDb.read(hash)).value());
    thenReturned(string);
  }
}
