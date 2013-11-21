package org.smoothbuild.io.cache.value;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Type;
import org.testory.common.Closure;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class CachedFileSetTest {
  ValueDb valueDb = mock(ValueDb.class);
  HashCode hash = HashCode.fromInt(33);
  File file = mock(File.class);

  CachedFileSet cachedFileSet;

  @Test
  public void null_object_db_is_forbidden() {
    when(newFileSetObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newFileSetObject(valueDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(cachedFileSet = new CachedFileSet(valueDb, hash));
    when(cachedFileSet.type());
    thenReturned(Type.FILE_SET);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(cachedFileSet = new CachedFileSet(valueDb, hash));
    when(cachedFileSet.hash());
    thenReturned(hash);
  }

  @Test
  public void iterator_is_taken_from_object_db() throws Exception {
    BDDMockito.given(valueDb.fileSetIterable(hash)).willReturn(ImmutableList.of(file));
    given(cachedFileSet = new CachedFileSet(valueDb, hash));
    when(ImmutableList.copyOf(cachedFileSet.iterator()));
    thenReturned(contains(file));
  }

  private static Closure newFileSetObject(final ValueDb valueDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachedFileSet(valueDb, hash);
      }
    };
  }
}
