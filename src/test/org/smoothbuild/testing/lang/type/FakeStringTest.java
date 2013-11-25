package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.testory.common.Closure;

public class FakeStringTest {
  String value = "value";
  String value2 = "value2";

  FakeString fakeString;

  @Test
  public void null_value_is_forbidden() throws Exception {
    when($fakeString(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void value_returns_value_passed_to_constructor() {
    given(fakeString = new FakeString(value));
    when(fakeString.value());
    thenReturned(value);
  }

  @Test
  public void type() throws Exception {
    given(fakeString = new FakeString(value));
    when(fakeString.type());
    thenReturned(STRING);
  }

  @Test
  public void fake_strings_with_different_values_have_different_hashes() throws Exception {
    given(fakeString = new FakeString(value));
    when(fakeString.hash());
    thenReturned(not(equalTo(new FakeString(value2).hash())));
  }

  private static Closure $fakeString(final String string) {
    return new Closure() {
      @Override
      public FakeString invoke() throws Throwable {
        return new FakeString(string);
      }
    };
  }
}
