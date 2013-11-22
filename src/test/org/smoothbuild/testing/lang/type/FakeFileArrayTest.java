package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.Iterables;

public class FakeFileArrayTest {
  FakeFile file;
  FakeFileArray fileArray = new FakeFileArray();

  @Test
  public void type() throws Exception {
    when(fileArray.type());
    thenReturned(Type.FILE_ARRAY);
  }

  @Test
  public void initially_file_array_is_empty() throws Exception {
    given(fileArray = new FakeFileArray());
    when(Iterables.size(fileArray));
    thenReturned(0);
  }

  @Test
  public void fake_file_array_contains_added_file() throws Exception {
    given(file = new FakeFile(path("my/file")));
    when(fileArray).add(file);
    then(fileArray, contains(file));
  }
}
