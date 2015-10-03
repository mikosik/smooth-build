package org.smoothbuild.parse;

import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.message.ContainsMessage.containsMessage;
import static org.smoothbuild.testing.message.ContainsOnlyMessageMatcher.containsOnlyMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.parse.err.SyntaxError;

public class ScriptParserTest {

  @Test
  public void parsing_empty_script_succeeds() throws Exception {
    assertParsingSucceeds("");
  }

  @Test
  public void parsing_simple_function_definition_succeeds() throws Exception {
    assertParsingSucceeds("functionA: functionB;");
  }

  @Test
  public void parsing_piped_function_definition_succeeds() throws Exception {
    assertParsingSucceeds("functionA: functionB | functionC;");
  }

  @Test
  public void parsing_function_definition_without_semicolon_fails() throws Exception {
    assertParsingFails("functionA : functionB ");
  }

  @Test
  public void parsing_function_definition_without_body_fails() throws Exception {
    assertParsingFails("functionA : ;");
  }

  @Test
  public void parsing_function_defined_as_string_literal_succeeds() throws Exception {
    assertParsingSucceeds(script("functionA : 'abc' ;"));
  }

  @Test
  public void parsing_empty_strings_succeeds() throws Exception {
    assertParsingSucceeds(script("functionA : '' ;"));
  }

  @Test
  public void parsing_not_closed_string_literal_fails() throws Exception {
    LoggedMessages messages = new LoggedMessages();
    try {
      runScriptParser(script("functionA : 'abc ;"), messages);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
      assertThat(messages, containsMessage(SyntaxError.class));
    }
  }

  @Test
  public void parsing_function_call_with_function_call_argument_succeeds() throws Exception {
    assertParsingSucceeds("functionA: functionB(param1=functionC);");
  }

  @Test
  public void parsing_function_call_with_string_literal_argument_succeeds() throws Exception {
    assertParsingSucceeds(script("functionA: functionB(param1='abc');"));
  }

  @Test
  public void parsing_incorrect_script_fails() throws Exception {
    assertParsingFails("abc");
  }

  private static void assertParsingSucceeds(String script) throws IOException {
    LoggedMessages messages = new LoggedMessages();
    try {
      runScriptParser(script, messages);
    } catch (PhaseFailedException e) {
      fail("no exception should be thrown");
    }
    assertThat(messages, emptyIterable());
  }

  private static void assertParsingFails(String script) throws IOException {
    LoggedMessages messages = new LoggedMessages();
    try {
      runScriptParser(script, messages);
      fail("exception should be thrown");
    } catch (PhaseFailedException e) {
      // expected
      assertThat(messages, containsOnlyMessage(SyntaxError.class));
    }
  }

  private static void runScriptParser(String string, LoggedMessages messages) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(CHARSET));
    ScriptParser.parseScript(messages, inputStream, path("filename.smooth"));
  }

  private static String script(String string) {
    return string.replace('\'', '"');
  }
}
