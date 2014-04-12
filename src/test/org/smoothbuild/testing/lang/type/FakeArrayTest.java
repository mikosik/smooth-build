package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SFile;

import com.google.common.collect.Iterables;

public class FakeArrayTest {
  FakeFile file;
  FakeFile file2;
  FakeArray<SFile> fileArray;

  @Test
  public void type() throws Exception {
    given(fileArray = new FakeArray<SFile>(FILE_ARRAY));
    when(fileArray.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void initially_file_array_is_empty() throws Exception {
    given(fileArray = new FakeArray<SFile>(FILE_ARRAY));
    when(Iterables.size(fileArray));
    thenReturned(0);
  }

  @Test
  public void fake_file_array_contains_added_file() throws Exception {
    given(fileArray = new FakeArray<SFile>(FILE_ARRAY));
    given(file = new FakeFile(path("my/file")));
    when(fileArray).add(file);
    then(fileArray, contains(file));
  }

  @Test
  public void fake_array_contains_files_passed_to_creation_method() throws Exception {
    given(file = new FakeFile(path("my/file")));
    given(file2 = new FakeFile(path("my/file2")));
    when(FakeArray.fakeArray(FILE_ARRAY, file, file2));
    thenReturned(contains(file, file2));
  }
}
