package org.smoothbuild.acceptance.lang;

import static java.util.regex.Pattern.DOTALL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.then;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ErrorTest extends AcceptanceTestCase {
  private static final String MY_MESSAGE = "my message";
  private String message;

  @Test
  public void file_system_exception_thrown_by_native_function_is_not_cached_as_error()
      throws IOException {
    givenScript("result = throwFileSystemException('" + MY_MESSAGE + "');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    given(message = getMessageNumber(output()));
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(getMessageNumber(output()), not(equalTo(message)));
  }

  @Test
  public void runtime_exception_thrown_by_native_function_is_not_cached_as_error()
      throws IOException {
    givenScript("result = throwRuntimeException('" + MY_MESSAGE + "');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    given(message = getMessageNumber(output()));
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(getMessageNumber(output()), not(equalTo(message)));
  }

  private String getMessageNumber(String output) {
    String outputPart = output.substring(output.indexOf(MY_MESSAGE) + MY_MESSAGE.length());
    Matcher matcher = Pattern.compile("(\\-?[0-9]+).*", DOTALL).matcher(outputPart);
    matcher.matches();
    return matcher.group(1);
  }
}
