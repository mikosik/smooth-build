package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.util.DecodingHexException;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public class BlobNode extends ExprNode {
  private final String literal;
  private ByteString byteString;

  public BlobNode(String literal, Location location) {
    super(location);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  public void decodeByteString() throws DecodingHexException {
    byteString = Hex.decode(literal);
  }

  public ByteString byteString() {
    return byteString;
  }
}
