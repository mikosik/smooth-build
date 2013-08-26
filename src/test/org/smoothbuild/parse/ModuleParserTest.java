package org.smoothbuild.parse;

import static com.google.common.base.Charsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class ModuleParserTest {

  @Test
  public void emptyScriptSucceeds() throws Exception {
    assertParsingSucceeds("");
  }

  @Test
  public void simpleFunctionDefinition() throws Exception {
    assertParsingSucceeds("functionA: functionB;");
  }

  @Test
  public void pipedFunctionDefinition() throws Exception {
    assertParsingSucceeds("functionA: functionB | functionC;");
  }

  @Test
  public void functionDefinitionWithoutSemicolonFails() throws Exception {
    parse("functionA : functionB ").assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void functionDefinitionWithoutBodyFails() throws Exception {
    parse("functionA : ;").assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void functionAssignedToStringLiteral() throws Exception {
    assertParsingSucceeds("functionA : \"abc\" ;");
  }

  @Test
  public void functionCallWithFunctionCallArgument() throws Exception {
    assertParsingSucceeds("functionA: functionB(param1=functionC);");
  }

  @Test
  public void functionCallWithStringLiteralArgument() throws Exception {
    assertParsingSucceeds("functionA: functionB(param1=\"abc\");");
  }

  @Test
  public void incorrectScriptFails() throws Exception {
    parse("abc").assertOnlyProblem(SyntaxError.class);
  }

  private static void assertParsingSucceeds(String scriptText) throws IOException {
    TestingProblemsListener problems = parse(scriptText);
    problems.assertNoProblems();
  }

  private static TestingProblemsListener parse(String string) throws IOException {
    ModuleParser parser = new ModuleParser();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(UTF_8));
    TestingProblemsListener problems = new TestingProblemsListener();
    parser.parse(inputStream, problems);
    return problems;
  }
}
