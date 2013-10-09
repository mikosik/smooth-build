package org.smoothbuild.parse;

import static com.google.inject.Guice.createInjector;

import org.junit.Test;
import org.smoothbuild.HashModule;

public class ImportedFunctionsProviderTest {
  @Test
  public void creatingImportedFunctionsThrowsNoException() {
    createInjector(new HashModule()).getInstance(ImportedFunctionsProvider.class).get();
  }
}
