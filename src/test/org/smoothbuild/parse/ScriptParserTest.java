package org.smoothbuild.parse;

import static com.google.common.base.Charsets.UTF_8;
import static org.smoothbuild.testing.ScriptBuilder.oneLineScript;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class ScriptParserTest {

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
    assertParsingFails("functionA : functionB ");
  }

  @Test
  public void functionDefinitionWithoutBodyFails() throws Exception {
    assertParsingFails("functionA : ;");
  }

  @Test
  public void functionDefinedAsStringLiteral() throws Exception {
    assertParsingSucceeds(oneLineScript("functionA : 'abc' ;"));
  }

  @Test
  public void emptyStringsAreAllowed() throws Exception {
    assertParsingSucceeds(oneLineScript("functionA : '' ;"));
  }

  @Test
  public void notClosedStringLiteralFails() throws Exception {
    parse(oneLineScript("functionA : 'abc ;")).assertProblemsFound();
  }

  @Test
  public void functionCallWithFunctionCallArgument() throws Exception {
    assertParsingSucceeds("functionA: functionB(param1=functionC);");
  }

  @Test
  public void functionCallWithStringLiteralArgument() throws Exception {
    assertParsingSucceeds(oneLineScript("functionA: functionB(param1='abc');"));
  }

  @Test
  public void incorrectScriptFails() throws Exception {
    assertParsingFails("abc");
  }

  private static void assertParsingSucceeds(String scriptText) throws IOException {
    TestingProblemsListener problems = parse(scriptText);
    problems.assertNoProblems();
  }

  private static void assertParsingFails(String script) throws IOException {
    TestingProblemsListener problems = parse(script);
    problems.assertOnlyProblem(SyntaxError.class);
  }

  private static TestingProblemsListener parse(String string) throws IOException {
    TestingProblemsListener problems = new TestingProblemsListener();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(UTF_8));
    ScriptParser.parseScript(problems, inputStream, "filename.smooth");
    return problems;
  }
}
