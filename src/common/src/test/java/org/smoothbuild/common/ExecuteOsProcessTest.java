package org.smoothbuild.common;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.ExecuteOsProcess.executeOsProcess;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ExecuteOsProcessTest {
  /**
   * This tests ensures that CommandExecutor closes all streams correctly so we won't suffer from
   * 'too many open files problem'. When it fails it provides misleading stack trace due to lack of
   * free file descriptors which prevent JVM from loading bytecode of appropriate exception class.
   */
  @Test
  @Disabled("lasts around 40 seconds")
  public void execution_closes_all_opened_streams() throws Exception {
    /*
     * This command should complete without errors on any OS.
     */
    String[] command = new String[] {"sleep", "0"};

    for (int i = 0; i < 10000; i++) {
      executeOsProcess(Path.of("."), command);
    }
  }

  @Test
  void running_unknown_binary_throws_IOException() throws Exception {
    try {
      executeOsProcess(Path.of("."), new String[] {"binary_file_that_does_not_exist"});
      fail("exception should be thrown");
    } catch (IOException e) {
      // expected
    }
  }

  @Test
  void failure_exit_code_is_returned() throws Exception {
    // linux command testing whether length of string "abc" is zero.
    // As it is not zero it will return non zero return code.
    String[] command = new String[] {"test", "-z", "abc"};
    assertThat(executeOsProcess(Path.of("."), command).exitCode()).isEqualTo(1);
  }

  @Test
  void success_exit_code_is_returned() throws Exception {
    // linux command testing whether length of string "abc" is not zero.
    // As it is not zero it will return zero return code.
    String[] command = new String[] {"test", "-n", "abc"};
    assertThat(executeOsProcess(Path.of("."), command).exitCode()).isEqualTo(0);
  }
}
