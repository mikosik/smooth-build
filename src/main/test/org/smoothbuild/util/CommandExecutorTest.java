package org.smoothbuild.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class CommandExecutorTest {
  /**
   * This tests ensures that CommandExecutor closes all streams correctly so we won't suffer from
   * 'too many open files problem'. When it fails it provides misleading stack trace due to lack of
   * free file descriptors which prevent JVM from loading bytecode of appropriate exception class.
   */
  @Test
  public void execution_closes_all_opened_streams() throws InterruptedException, IOException {
    /**
     * This command should complete without errors on any OS.
     */
    List<String> command = list("sleep", "0");

    for (int i = 0; i < 10000; i++) {
      CommandExecutor.execute(command);
    }
  }

  @Test
  public void runing_unknown_binary_throws_IOException() throws Exception {
    try {
      CommandExecutor.execute(list("binary_file_that_does_not_exist"));
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void failure_exit_code_is_returned() throws Exception {
    // linux command testing whether length of string "abc" is zero.
    // As it is not zero it will return non zero return code.
    List<String> command = list("test", "-z", "abc");
    assertEquals(1, CommandExecutor.execute(command));
  }

  @Test
  public void success_exit_code_is_returned() throws Exception {
    // linux command testing whether length of string "abc" is not zero.
    // As it is not zero it will return zero return code.
    List<String> command = list("test", "-n", "abc");
    assertEquals(0, CommandExecutor.execute(command));
  }
}
