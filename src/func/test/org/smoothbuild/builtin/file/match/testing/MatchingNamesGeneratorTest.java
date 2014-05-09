package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.builtin.file.match.testing.MatchingNamesGenerator.generateNames;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.testory.Closure;

import com.google.common.base.Function;

public class MatchingNamesGeneratorTest {

  @Test
  public void test() {
    List<String> expected = newArrayList();
    expected.add("xz");

    expected.add("xaz");
    expected.add("xbz");
    expected.add("xcz");

    expected.add("xaaz");
    expected.add("xabz");
    expected.add("xacz");

    expected.add("xbaz");
    expected.add("xbbz");
    expected.add("xbcz");

    expected.add("xcaz");
    expected.add("xcbz");
    expected.add("xccz");

    String[] expectedNames = expected.toArray(new String[] {});

    when(new Closure() {
      @Override
      public List<String> invoke() throws Throwable {
        CollectingConsumer collectingConsumer = new CollectingConsumer();
        generateNames("x*z", collectingConsumer);
        return collectingConsumer.generatedNames;
      }
    });
    thenReturned(contains(expectedNames));
  }

  private static class CollectingConsumer implements Function<String, Void> {
    private final List<String> generatedNames = newArrayList();

    @Override
    public Void apply(String name) {
      generatedNames.add(name);
      return null;
    }
  }
}
