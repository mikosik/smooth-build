package org.smoothbuild.object;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.plugin.File;
import org.testory.common.Closure;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class FileSetObjectTest {
  ObjectDb objectDb = mock(ObjectDb.class);
  HashCode hash = HashCode.fromInt(33);
  File file = mock(File.class);

  FileSetObject fileSetObject;

  @Test
  public void null_object_db_is_forbidden() {
    when(newFileSetObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newFileSetObject(objectDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(fileSetObject = new FileSetObject(objectDb, hash));
    when(fileSetObject.hash());
    thenReturned(hash);
  }

  @Test
  public void iterator_is_taken_from_object_db() throws Exception {
    BDDMockito.given(objectDb.fileSetIterable(hash)).willReturn(ImmutableList.of(file));
    given(fileSetObject = new FileSetObject(objectDb, hash));
    when(ImmutableList.copyOf(fileSetObject.iterator()));
    thenReturned(contains(file));
  }

  private static Closure newFileSetObject(final ObjectDb objectDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new FileSetObject(objectDb, hash);
      }
    };
  }
}
