package org.smoothbuild.parse;

import static com.google.common.base.Charsets.UTF_8;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.Lists;

public class SyntaxParserTest {

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
    assertOnlyProblem("functionA : functionB ", SyntaxError.class);
  }

  @Test
  public void functionDefinitionWithoutBodyFails() throws Exception {
    assertOnlyProblem("functionA : ;", SyntaxError.class);
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
    assertOnlyProblem("abc", SyntaxError.class);
  }

  private static void assertParsingSucceeds(String scriptText) throws IOException {
    List<Problem> collected = parse(scriptText).collected();
    if (!collected.isEmpty()) {
      Problem problem = collected.get(0);
      Assert.fail("Expected zero problems,\nbut got message:\n" + problem.message()
          + "\nlocation: " + problem.sourceLocation());
    }
  }

  private static void assertOnlyProblem(String string, Class<SyntaxError> klass) throws IOException {
    List<Problem> problems = parse(string).collected();
    assertThat(problems.size()).isEqualTo(1);
    assertThat(problems.get(0)).isInstanceOf(klass);
  }

  private static CollecedProblems parse(String string) throws IOException {
    SyntaxParser parser = new SyntaxParser();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(UTF_8));
    CollecedProblems problems = new CollecedProblems();
    parser.parse(inputStream, problems);
    return problems;
  }

  private static class CollecedProblems implements ProblemsListener {
    private final List<Problem> list = Lists.newArrayList();

    @Override
    public void report(Problem problem) {
      list.add(problem);
    }

    public List<Problem> collected() {
      return list;
    }
  }
}
