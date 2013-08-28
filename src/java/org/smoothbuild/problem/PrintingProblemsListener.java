package org.smoothbuild.problem;

import java.io.PrintStream;

public class PrintingProblemsListener implements ProblemsListener {
  private final PrintStream printStream;

  public PrintingProblemsListener() {
    this(System.out);
  }

  public PrintingProblemsListener(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void report(Problem problem) {
    printStream.println(problem.toString());
  }
}
