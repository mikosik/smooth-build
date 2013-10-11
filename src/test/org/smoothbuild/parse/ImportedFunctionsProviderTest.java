package org.smoothbuild.parse;

import static com.google.inject.Guice.createInjector;

import org.junit.Test;

public class ImportedFunctionsProviderTest {
  @Test
  public void creatingImportedFunctionsThrowsNoException() {
    createInjector().getInstance(ImportedFunctionsProvider.class).get();
  }
}
