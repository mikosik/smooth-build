package org.smoothbuild.object;

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
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.plugin.FakeString;

import com.google.common.hash.HashCode;

public class ResultDbTest {
  HashedDb taskToResultDb = new HashedDb(new FakeFileSystem());
  ValueDb valueDb = new ValueDb(new HashedDb(new FakeFileSystem()));
  ResultDb resultDb = new ResultDb(taskToResultDb, valueDb);
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
    when(resultDb.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_stored_result() {
    given(resultDb).store(hash, new FakeString("result"));
    when(resultDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    when(resultDb).read(hash);
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  @Test
  public void stored_file_set_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(fileSet = valueDb.fileSet(newArrayList(file)));
    given(resultDb).store(hash, fileSet);
    when(((FileSet) resultDb.read(hash)).iterator().next());
    thenReturned(equalTo(file));
  }

  @Test
  public void stored_string_set_can_be_read_back() throws Exception {
    given(stringValue = valueDb.string(string));
    given(stringSet = valueDb.stringSet(newArrayList(stringValue)));
    given(resultDb).store(hash, stringSet);
    when(resultDb.read(hash));
    thenReturned(containsOnly(string));
  }

  @Test
  public void stored_file_can_be_read_back() throws Exception {
    given(file = valueDb.file(path, bytes));
    given(resultDb).store(hash, file);
    when(resultDb.read(hash));
    thenReturned(equalTo(file));
  }

  @Test
  public void stored_string_object_can_be_read_back() throws Exception {
    given(stringValue = valueDb.string(string));
    given(resultDb).store(hash, stringValue);
    when(((StringValue) resultDb.read(hash)).value());
    thenReturned(string);
  }
}
