package org.smoothbuild.parse;

import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothContants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.parse.ScriptBuilder.script;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.message.FakeLoggedMessages;

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
    assertParsingSucceeds(script("functionA : 'abc' ;"));
  }

  @Test
  public void emptyStringsAreAllowed() throws Exception {
    assertParsingSucceeds(script("functionA : '' ;"));
  }

  @Test
  public void notClosedStringLiteralFails() throws Exception {
    FakeLoggedMessages messages = new FakeLoggedMessages();
    try {
      runScriptParser(script("functionA : 'abc ;"), messages);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
      messages.assertContains(SyntaxError.class);
    }
  }

  @Test
  public void functionCallWithFunctionCallArgument() throws Exception {
    assertParsingSucceeds("functionA: functionB(param1=functionC);");
  }

  @Test
  public void functionCallWithStringLiteralArgument() throws Exception {
    assertParsingSucceeds(script("functionA: functionB(param1='abc');"));
  }

  @Test
  public void incorrectScriptFails() throws Exception {
    assertParsingFails("abc");
  }

  private static void assertParsingSucceeds(String script) throws IOException {
    FakeLoggedMessages messages = new FakeLoggedMessages();
    try {
      runScriptParser(script, messages);
    } catch (PhaseFailedException e) {
      messages.assertNoProblems();
      fail("no exception should be thrown");
    }
    messages.assertNoProblems();
  }

  private static void assertParsingFails(String script) throws IOException {
    FakeLoggedMessages messages = new FakeLoggedMessages();
    try {
      runScriptParser(script, messages);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
      messages.assertContainsOnly(SyntaxError.class);
    }
  }

  private static void runScriptParser(String string, LoggedMessages messages) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(CHARSET));
    ScriptParser.parseScript(messages, inputStream, path("filename.smooth"));
  }
}
