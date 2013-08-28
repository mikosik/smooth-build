package org.smoothbuild.problem;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.problem.ProblemType.ERROR;

import java.io.PrintStream;

import org.junit.Test;

public class PrintingProblemsListenerTest {
  PrintStream printStream = mock(PrintStream.class);
  PrintingProblemsListener printingProblemsListener = new PrintingProblemsListener(printStream);

  @Test
  public void problemIsPrinted() throws Exception {
    String message = "message string";
    printingProblemsListener.report(new MyProblem(message));
    verify(printStream).println(message);
  }

  public static class MyProblem extends Problem {
    private final String toStrigValue;

    public MyProblem(String toStringValue) {
      super(ERROR, null, "");
      this.toStrigValue = toStringValue;
    }

    @Override
    public String toString() {
      return toStrigValue;
    }
  }
}
