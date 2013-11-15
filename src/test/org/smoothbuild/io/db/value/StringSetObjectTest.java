package org.smoothbuild.io.db.value;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.plugin.StringValue;
import org.testory.common.Closure;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class StringSetObjectTest {
  ValueDb valueDb = mock(ValueDb.class);
  HashCode hash = HashCode.fromInt(33);
  StringValue string = mock(StringValue.class);

  StringSetObject stringSetObject;

  @Test
  public void null_object_db_is_forbidden() {
    when(newStringSetObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newStringSetObject(valueDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(stringSetObject = new StringSetObject(valueDb, hash));
    when(stringSetObject.type());
    thenReturned(Type.STRING_SET);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(stringSetObject = new StringSetObject(valueDb, hash));
    when(stringSetObject.hash());
    thenReturned(hash);
  }

  @Test
  public void iterator_is_taken_from_object_db() throws Exception {
    BDDMockito.given(valueDb.stringSetIterable(hash)).willReturn(ImmutableList.of(string));
    given(stringSetObject = new StringSetObject(valueDb, hash));
    when(ImmutableList.copyOf(stringSetObject.iterator()));
    thenReturned(contains(string));
  }

  private static Closure newStringSetObject(final ValueDb valueDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new StringSetObject(valueDb, hash);
      }
    };
  }

}
