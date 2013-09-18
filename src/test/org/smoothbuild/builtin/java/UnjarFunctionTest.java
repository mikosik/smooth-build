package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.builtin.java.err.IllegalPathInJarException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class UnjarFunctionTest {

  TestSandbox sandbox = new TestSandbox();
  UnjarFunction.Parameters parameters = mock(UnjarFunction.Parameters.class);
  File file = mock(File.class);
  Unjarer unjarer = mock(Unjarer.class);

  @Test
  public void testUnjaring() throws Exception {
    when(parameters.file()).thenReturn(file);

    FileSet result = new UnjarFunction.Worker(unjarer).execute(sandbox, parameters);

    assertThat(result).isSameAs(sandbox.resultFileSet());
    verify(unjarer).unjarFile(file, sandbox.resultFileSet());
  }

  @Test
  public void ioExceptionIsReported() throws Exception {
    when(parameters.file()).thenReturn(file);
    doThrow(IOException.class).when(unjarer).unjarFile(file, sandbox.resultFileSet());

    try {
      new UnjarFunction.Worker(unjarer).execute(sandbox, parameters);
      fail("exception should be thrown");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void illegalPathInJarExceptionIsReported() throws Exception {
    when(parameters.file()).thenReturn(file);
    doThrow(IllegalPathInJarException.class).when(unjarer).unjarFile(file, sandbox.resultFileSet());

    new UnjarFunction.Worker(unjarer).execute(sandbox, parameters);
    sandbox.messages().assertOnlyProblem(IllegalPathInJarError.class);
  }
}
