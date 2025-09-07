package org.smoothbuild.systemtest;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.base.Strings.convertOsLineSeparatorsToNewLine;
import static org.smoothbuild.common.base.Strings.unlines;

public record SystemTestOutput(int exitCode, String systemOut, String systemErr) {
  public void assertFinishedWithSuccess() {
    assertReturnedCode(EXIT_CODE_SUCCESS);
  }

  public void assertFinishedWithError() {
    assertReturnedCode(EXIT_CODE_ERROR);
  }

  private void assertReturnedCode(int expected) {
    if (expected != exitCode) {
      fail("Expected return code " + expected + " but was " + exitCode + ".\n"
          + "standard out:\n" + systemOut + "\n"
          + "standard err:\n" + systemErr + "\n");
    }
  }

  public void assertSystemOutContains(String text) {
    assertWithFullOutputs(systemOut, text, "SystemOut");
  }

  public void assertSystemErrContains(String text) {
    assertWithFullOutputs(systemErr, text, "SystemErr");
  }

  private void assertWithFullOutputs(String out, String text, String outName) {
    var convertedOut = convertOsLineSeparatorsToNewLine(out);
    if (!convertedOut.contains(text)) {
      assertWithMessage(unlines(
              outName + " doesn't contain expected substring.",
              "================= SYSTEM-OUT START ====================",
              systemOut,
              "================= SYSTEM-OUT END   ====================",
              "================= SYSTEM-ERR START ====================",
              systemErr,
              "================= SYSTEM-ERR END   ===================="))
          .that(convertedOut)
          .isEqualTo(text);
    }
  }

  public void assertSystemOutDoesNotContain(String text) {
    assertWithMessage(unlines(
            "SystemOut contains forbidden substring",
            "================= SYSTEM-OUT START ====================",
            systemOut,
            "================= SYSTEM-OUT END   ====================",
            "================= SYSTEM-ERR START ====================",
            systemErr,
            "================= SYSTEM-ERR END   ===================="))
        .that(convertOsLineSeparatorsToNewLine(systemOut))
        .doesNotContain(text);
  }
}
