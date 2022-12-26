package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobP extends ExprP {
  private final String literal;
  private ByteString byteString;

  public BlobP(String literal, Location location) {
    super(location);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  public void decodeByteString() throws DecodeHexExc {
    byteString = Hex.decode(literal);
  }

  public ByteString byteString() {
    return byteString;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof BlobP that
        && Objects.equals(this.literal, that.literal)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(literal, location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "literal = " + literal,
        "location = " + location()
    );
    return "BlobP(\n" + indent(fields) + "\n)";
  }
}
