package org.smoothbuild.testing;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

import org.junit.jupiter.api.Test;

public class StringCreatorsTest {
  @Test
  public void illegal_string_cannot_be_decoded() {
    CharsetDecoder charsetDecoder = CHARSET.newDecoder();
    charsetDecoder.onMalformedInput(REPORT);
    assertCall(() -> charsetDecoder.decode(wrap(illegalString().toByteArray())))
        .throwsException(MalformedInputException.class);
  }
}
