package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.Iterables;

public class FakeArrayTest {
  FakeFile file;
  FakeFile file2;
  FakeArray<SFile> array;

  @Test
  public void type() throws Exception {
    given(array = new FakeArray<SFile>(FILE_ARRAY));
    when(array.type());
    thenReturned(FILE_ARRAY);
  }

  @Test
  public void initially_file_array_is_empty() throws Exception {
    given(array = new FakeArray<SFile>(FILE_ARRAY));
    when(Iterables.size(array));
    thenReturned(0);
  }

  @Test
  public void fake_file_array_contains_added_file() throws Exception {
    given(array = new FakeArray<SFile>(FILE_ARRAY));
    given(file = new FakeFile(path("my/file")));
    when(array).add(file);
    then(array, contains(file));
  }

  @Test
  public void fake_array_contains_files_passed_to_creation_method() throws Exception {
    given(file = new FakeFile(path("my/file")));
    given(file2 = new FakeFile(path("my/file2")));
    when(FakeArray.fakeArray(FILE_ARRAY, file, file2));
    thenReturned(contains(file, file2));
  }

  @Test
  public void fake_array_hash_is_compatible_with_array_object_hash() throws Exception {
    given(file = new FakeFile(path("my/file")));
    given(file2 = new FakeFile(path("my/file2")));
    given(array = FakeArray.fakeArray(FILE_ARRAY, file, file2));
    when(array).hash();
    thenReturned(objectArray(file, file2).hash());
  }

  private static SArray<SFile> objectArray(SFile file1, SFile file2) {
    return new FakeObjectsDb().arrayBuilder(FILE_ARRAY).add(file1).add(file2).build();
  }
}
