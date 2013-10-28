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
import org.smoothbuild.hash.Hash;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.plugin.FakeString;

import com.google.common.hash.HashCode;

public class ResultCacheTest {
  HashedDb taskResultDb = new HashedDb(new FakeFileSystem());
  ObjectDb objectDb = new ObjectDb(new HashedDb(new FakeFileSystem()));
  ResultCache resultCache = new ResultCache(taskResultDb, objectDb);
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
    when(resultCache.contains(hash));
    thenReturned(false);
  }

  @Test
  public void result_cache_contains_stored_result() {
    given(resultCache).store(hash, new FakeString("result"));
    when(resultCache.contains(hash));
    thenReturned(true);
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    when(resultCache).read(hash);
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }

  @Test
  public void stored_file_set_can_be_read_back() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(fileSet = objectDb.fileSet(newArrayList(file)));
    given(resultCache).store(hash, fileSet);
    when(((FileSet) resultCache.read(hash)).iterator().next());
    thenReturned(equalTo(file));
  }

  @Test
  public void stored_string_set_can_be_read_back() throws Exception {
    given(stringValue = objectDb.string(string));
    given(stringSet = objectDb.stringSet(newArrayList(stringValue)));
    given(resultCache).store(hash, stringSet);
    when(resultCache.read(hash));
    thenReturned(containsOnly(string));
  }

  @Test
  public void stored_file_can_be_read_back() throws Exception {
    given(file = objectDb.file(path, bytes));
    given(resultCache).store(hash, file);
    when(resultCache.read(hash));
    thenReturned(equalTo(file));
  }

  @Test
  public void stored_string_object_can_be_read_back() throws Exception {
    given(stringValue = objectDb.string(string));
    given(resultCache).store(hash, stringValue);
    when(((StringValue) resultCache.read(hash)).value());
    thenReturned(string);
  }
}
