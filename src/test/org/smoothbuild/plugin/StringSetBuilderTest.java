package org.smoothbuild.plugin;

import static org.hamcrest.Matchers.emptyIterable;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.db.value.FakeValueDb;

import com.google.common.collect.Iterables;

public class StringSetBuilderTest {
  FakeValueDb objectDb = new FakeValueDb();

  StringSetBuilder stringSetBuilder = new StringSetBuilder(objectDb);
  StringValue stringValue;
  String string = "some string";

  @Test
  public void build_returns_empty_string_set_when_no_string_has_been_added() throws IOException {
    given(stringSetBuilder = new StringSetBuilder(objectDb));
    when(stringSetBuilder.build());
    thenReturned(emptyIterable());
  }

  @Test
  public void returned_string_set_contains_one_element_when_one_string_has_been_added()
      throws Exception {
    given(stringValue = objectDb.string(string));
    given(stringSetBuilder).add(stringValue);
    when(Iterables.size(stringSetBuilder.build()));
    thenReturned(1);
  }

  @Test
  public void returned_string_set_contains_added_element() throws Exception {
    given(stringValue = objectDb.string(string));
    given(stringSetBuilder).add(stringValue);
    when(stringSetBuilder.build().iterator().next().value());
    thenReturned(stringValue.value());
  }
}
