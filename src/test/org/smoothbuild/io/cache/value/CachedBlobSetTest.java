package org.smoothbuild.io.cache.value;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.Type;
import org.testory.common.Closure;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class CachedBlobSetTest {
  ValueDb valueDb = mock(ValueDb.class);
  HashCode hash = HashCode.fromInt(33);
  Blob blob = mock(Blob.class);

  CachedBlobSet cachedBlobSet;

  @Test
  public void null_object_db_is_forbidden() {
    when(newBlobSetObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newBlobSetObject(valueDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(cachedBlobSet = new CachedBlobSet(valueDb, hash));
    when(cachedBlobSet.type());
    thenReturned(Type.BLOB_SET);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(cachedBlobSet = new CachedBlobSet(valueDb, hash));
    when(cachedBlobSet.hash());
    thenReturned(hash);
  }

  @Test
  public void iterator_is_taken_from_object_db() throws Exception {
    BDDMockito.given(valueDb.blobSetIterable(hash)).willReturn(ImmutableList.of(blob));
    given(cachedBlobSet = new CachedBlobSet(valueDb, hash));
    when(ImmutableList.copyOf(cachedBlobSet.iterator()));
    thenReturned(contains(blob));
  }

  private static Closure newBlobSetObject(final ValueDb valueDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachedBlobSet(valueDb, hash);
      }
    };
  }
}
