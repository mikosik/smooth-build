package org.smoothbuild.testing.lang.function.value;

import java.util.Arrays;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.lang.function.value.StringSet;
import org.smoothbuild.lang.function.value.StringValue;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class StringSetMatchers {
  public static Matcher<StringSet> containsOnly(final String... strings) {
    return new TypeSafeMatcher<StringSet>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("StringSet containing only " + Arrays.toString(strings));
      }

      @Override
      protected boolean matchesSafely(StringSet stringSet) {
        ImmutableSet<String> actual = valuesSet(stringSet);
        ImmutableSet<String> expected = ImmutableSet.copyOf(strings);
        return actual.equals(expected);
      }

      private ImmutableSet<String> valuesSet(StringSet stringSet) {
        Builder<String> builder = ImmutableSet.builder();
        for (StringValue stringValue : stringSet) {
          builder.add(stringValue.value());
        }
        return builder.build();
      }
    };
  }
}
