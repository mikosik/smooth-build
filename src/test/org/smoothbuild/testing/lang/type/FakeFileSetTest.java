package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.lang.type.FakeFileSet;

import com.google.common.collect.Iterables;

public class FakeFileSetTest {
  FakeFile file;
  FakeFileSet fileSet = new FakeFileSet();

  @Test
  public void type() throws Exception {
    when(fileSet.type());
    thenReturned(Type.FILE_ARRAY);
  }

  @Test
  public void initially_file_set_is_empty() throws Exception {
    given(fileSet = new FakeFileSet());
    when(Iterables.size(fileSet));
    thenReturned(0);
  }

  @Test
  public void fake_file_set_contains_added_file() throws Exception {
    given(file = new FakeFile(path("my/file")));
    when(fileSet).add(file);
    then(fileSet, contains(file));
  }
}
