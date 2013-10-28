package org.smoothbuild.parse;

import static com.google.inject.Guice.createInjector;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestModule;

public class ImportedFunctionsProviderTest {
  @Test
  public void creatingImportedFunctionsThrowsNoException() {
    createInjector(new IntegrationTestModule()).getInstance(ImportedFunctionsProvider.class).get();
  }
}
