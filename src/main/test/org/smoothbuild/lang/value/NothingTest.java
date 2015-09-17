package org.smoothbuild.lang.value;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class NothingTest {
  @Test
  public void nothing_cannot_be_instantiated() {
    for (Constructor<?> constructor : Nothing.class.getDeclaredConstructors()) {
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
  }
}
