package org.smoothbuild.lang.function;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;

public class FunctionsTest {
  private Functions functions;

  @Test
  public void names_returns_collection_that_forbids_removing_elements() {
    given(functions = new Functions());
    when(functions.names()).remove(null);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void names_returns_collection_that_forbids_adding_elements() {
    given(functions = new Functions());
    when(functions.names()).add(name("name"));
    thenThrown(UnsupportedOperationException.class);
  }
}
