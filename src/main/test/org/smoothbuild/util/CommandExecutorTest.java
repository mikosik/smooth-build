package org.smoothbuild.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class CommandExecutorTest {

  /**
   * This tests ensures that CommandExecutor closes all streams correctly so we
   * won't suffer from 'too many open files problem'. When it fails it provides
   * misleading stack trace due to lack of free file descriptors which prevent
   * JVM from loading bytecode of appropriate exception class.
   */
  @Test
  public void execution_closes_all_opened_streams() throws InterruptedException, IOException {
    /**
     * This command should complete without errors on any OS.
     */
    ImmutableList<String> command = ImmutableList.of("sleep", "0");

    for (int i = 0; i < 10000; i++) {
      CommandExecutor.execute(command);
    }
  }

  @Test
  public void runing_unknown_binary_throws_IOException() throws Exception {
    try {
      CommandExecutor.execute(ImmutableList.of("binary_file_that_does_not_exist"));
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  public void failure_exit_code_is_returned() throws Exception {
    // linux command testing whether length of string "abc" is zero.
    // As it is not zero it will return non zero return code.
    ImmutableList<String> command = ImmutableList.of("test", "-z", "abc");
    assertEquals(1, CommandExecutor.execute(command));
  }

  @Test
  public void success_exit_code_is_returned() throws Exception {
    // linux command testing whether length of string "abc" is not zero.
    // As it is not zero it will return zero return code.
    ImmutableList<String> command = ImmutableList.of("test", "-n", "abc");
    assertEquals(0, CommandExecutor.execute(command));
  }
}
