package org.smoothbuild.parse.ast;

import static org.smoothbuild.testing.TestingContext.loc;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class MonoNamedNTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(namedNode(11, "equal"), namedNode(11, "equal"));
    tester.addEqualityGroup(namedNode(1, "a"), namedNode(2, "a"), namedNode(3, "a"));
    tester.addEqualityGroup(namedNode(1, "b"), namedNode(2, "b"), namedNode(3, "b"));
    tester.addEqualityGroup(namedNode(1, "ab"), namedNode(2, "ab"), namedNode(3, "ab"));
    tester.addEqualityGroup(namedNode(1, "ba"), namedNode(2, "ba"), namedNode(3, "ba"));
    tester.testEquals();
  }

  private static MonoNamedN namedNode(int line, String name) {
    return new MonoNamedN(name, loc(line));
  }
}
