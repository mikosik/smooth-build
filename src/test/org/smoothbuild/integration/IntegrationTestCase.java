package org.smoothbuild.integration;

import static com.google.inject.Guice.createInjector;

import org.junit.Before;
import org.smoothbuild.run.SmoothRunner;
import org.smoothbuild.testing.TestingFileSystem;
import org.smoothbuild.testing.TestingFileSystemModule;
import org.smoothbuild.testing.problem.TestingProblemsListener;
import org.smoothbuild.testing.problem.TestingProblemsListenerModule;

import com.google.inject.Injector;

public class IntegrationTestCase {

  TestingFileSystem fileSystem;
  SmoothRunner smoothRunner;
  TestingProblemsListener problems;

  @Before
  public void before() {
    Injector injector = createInjector(new TestingFileSystemModule(),
        new TestingProblemsListenerModule());
    fileSystem = injector.getInstance(TestingFileSystem.class);
    problems = injector.getInstance(TestingProblemsListener.class);
    smoothRunner = injector.getInstance(SmoothRunner.class);
  }
}
