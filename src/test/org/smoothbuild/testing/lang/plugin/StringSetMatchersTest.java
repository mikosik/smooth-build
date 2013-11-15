package org.smoothbuild.testing.lang.plugin;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.Iterator;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.StringSet;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.testing.lang.function.value.FakeString;
import org.smoothbuild.testing.lang.function.value.StringSetMatchers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class StringSetMatchersTest {
  String string1 = "string1";
  String string2 = "string2";
  Matcher<StringSet> matcher;

  @Test
  public void matcher_matches_when_string_set_contains_asserted_strings() {
    given(matcher = StringSetMatchers.containsOnly(string1, string2));
    when(matcher.matches(stringSet(string1, string2)));
    thenReturned(true);
  }

  @Test
  public void matcher_matches_when_string_set_is_empty_and_asserted_strings_are_empty() {
    given(matcher = StringSetMatchers.containsOnly());
    when(matcher.matches(stringSet()));
    thenReturned(true);
  }

  @Test
  public void matcher_does_not_match_when_string_set_contains_asserted_string_and_one_more() {
    given(matcher = StringSetMatchers.containsOnly(string1));
    when(matcher.matches(stringSet(string1, string2)));
    thenReturned(false);
  }

  @Test
  public void matcher_does_not_match_when_string_set_contains_different_value() {
    given(matcher = StringSetMatchers.containsOnly(string1));
    when(matcher.matches(stringSet(string2)));
    thenReturned(false);
  }

  @Test
  public void matcher_does_not_match_when_string_set_is_empty() {
    given(matcher = StringSetMatchers.containsOnly(string1));
    when(matcher.matches(stringSet()));
    thenReturned(false);
  }

  private static MyStringSet stringSet(String... strings) {
    return new MyStringSet(strings);
  }

  private static class MyStringSet implements StringSet {
    private final ImmutableList<StringValue> values;

    public MyStringSet(String... values) {
      this.values = createValues(values);
    }

    private static ImmutableList<StringValue> createValues(String... strings) {
      Builder<StringValue> builder = ImmutableList.builder();
      for (String string : strings) {
        builder.add(new FakeString(string));
      }
      return builder.build();
    }

    @Override
    public Iterator<StringValue> iterator() {
      return values.iterator();
    }

    @Override
    public HashCode hash() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Type type() {
      return Type.STRING_SET;
    }
  }
}
