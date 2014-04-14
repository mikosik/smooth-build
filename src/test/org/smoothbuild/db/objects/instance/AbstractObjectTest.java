package org.smoothbuild.db.objects.instance;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SType;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class AbstractObjectTest {
  HashCode hash = HashCode.fromInt(123);
  HashCode hash2 = HashCode.fromInt(124);

  AbstractObject abstractValue;
  AbstractObject abstractValue2;

  @Test
  public void null_type_is_forbidden() {
    when($abstractValue(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when($abstractValue(STRING, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_returns_type() throws Exception {
    given(abstractValue = new MyAbstractValue(STRING, hash));
    when(abstractValue.type());
    thenReturned(STRING);
  }

  @Test
  public void hash_returns_hash() throws Exception {
    given(abstractValue = new MyAbstractValue(STRING, hash));
    when(abstractValue.hash());
    thenReturned(hash);
  }

  @Test
  public void values_with_the_same_hash_are_equal() throws Exception {
    given(abstractValue = new MyAbstractValue(STRING, hash));
    given(abstractValue2 = new MyAbstractValue(BLOB, hash));
    when(abstractValue.equals(abstractValue2));
    thenReturned(true);
  }

  @Test
  public void values_with_the_different_hash_are_not_equal() throws Exception {
    given(abstractValue = new MyAbstractValue(STRING, hash));
    given(abstractValue2 = new MyAbstractValue(STRING, hash2));
    when(abstractValue.equals(abstractValue2));
    thenReturned(false);
  }

  private static Closure $abstractValue(final Object object, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyAbstractValue(null, hash);
      }
    };
  }

  private static class MyAbstractValue extends AbstractObject {
    public MyAbstractValue(SType<?> type, HashCode hash) {
      super(type, hash);
    }
  }
}
