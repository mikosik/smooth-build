package org.smoothbuild.object;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.type.api.File;
import org.testory.common.Closure;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class FileSetObjectTest {
  ObjectsDb objectsDb = mock(ObjectsDb.class);
  HashCode hash = HashCode.fromInt(33);
  File file = mock(File.class);

  FileSetObject fileSetObject;

  @Test
  public void null_objects_db_is_forbidden() {
    when(newFileSetObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newFileSetObject(objectsDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(fileSetObject = new FileSetObject(objectsDb, hash));
    when(fileSetObject.hash());
    thenReturned(hash);
  }

  @Test
  public void iterator_is_taken_from_object_db() throws Exception {
    BDDMockito.given(objectsDb.fileSetIterable(hash)).willReturn(ImmutableList.of(file));
    given(fileSetObject = new FileSetObject(objectsDb, hash));
    when(ImmutableList.copyOf(fileSetObject.iterator()));
    thenReturned(contains(file));
  }

  private static Closure newFileSetObject(final ObjectsDb objectsDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new FileSetObject(objectsDb, hash);
      }
    };
  }
}
