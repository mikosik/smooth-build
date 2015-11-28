package org.smoothbuild.lang.message;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class MessageTest {
  private String messageString;
  private Message message;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void test_error() {
    when(message = new ErrorMessage(messageString));
    thenEqual(message.getMessage(), messageString);
  }

  @Test
  public void to_string() throws Exception {
    given(message = new ErrorMessage("my-message"));
    when(message.toString());
    thenReturned("ERROR: my-message");
  }

  @Test
  public void equals_and_hash_code() throws Exception {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new WarningMessage("equal"), new WarningMessage("equal"));
    tester.addEqualityGroup(new ErrorMessage("equal"));
    tester.addEqualityGroup(new ErrorMessage("not equal"));
    tester.addEqualityGroup(new InfoMessage("equal"));
    tester.addEqualityGroup(new InfoMessage("not equal"));
    tester.testEquals();
  }
}
