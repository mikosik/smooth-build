package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class UnzipFunctionTest {
  TestSandbox sandbox = new TestSandbox();
  UnzipFunction.Parameters parameters = mock(UnzipFunction.Parameters.class);
  File file = mock(File.class);
  Unzipper unzipper = mock(Unzipper.class);

  @Test
  public void testUnzipping() throws Exception {
    when(parameters.file()).thenReturn(file);

    FileSet result = new UnzipFunction.Worker(unzipper).execute(sandbox, parameters);

    assertThat(result).isSameAs(sandbox.resultFileSet());
    verify(unzipper).unzipFile(file, sandbox.resultFileSet());
  }

  @Test
  public void ioExceptionIsReported() throws Exception {
    when(parameters.file()).thenReturn(file);
    doThrow(IOException.class).when(unzipper).unzipFile(file, sandbox.resultFileSet());

    try {
      new UnzipFunction.Worker(unzipper).execute(sandbox, parameters);
      fail("exception should be thrown");
    } catch (FileSystemException e) {
      // expected
    }
  }

  @Test
  public void illegalPathInZipExceptionIsReported() throws Exception {
    when(parameters.file()).thenReturn(file);
    doThrow(IllegalPathInZipException.class).when(unzipper)
        .unzipFile(file, sandbox.resultFileSet());

    new UnzipFunction.Worker(unzipper).execute(sandbox, parameters);
    sandbox.messages().assertOnlyProblem(IllegalPathInZipError.class);
  }
}
