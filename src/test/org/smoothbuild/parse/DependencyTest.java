package org.smoothbuild.parse;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class DependencyTest {

  @Test
  public void testEqualsAndHashCode() {
    EqualsVerifier.forClass(Dependency.class).suppress(NULL_FIELDS).verify();
  }
}
