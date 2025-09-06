package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import java.math.BigInteger;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;

public final class PPosition implements HasLocation {
  private final String literal;
  private final Location location;
  private @Nullable BigInteger bigInteger;

  public PPosition(String literal, Location location) {
    this.literal = literal;
    this.location = location;
  }

  public void decodeBigInteger() throws NumberFormatException {
    bigInteger = PInt.decodeAsBigInteger(literal);
    if (bigInteger.compareTo(BigInteger.ONE) < 0) {
      throw new NumberFormatException();
    }
  }

  public String literal() {
    return literal;
  }

  @Override
  public Location location() {
    return location;
  }

  public BigInteger bigInteger() {
    return checkInitializedToNotNull(bigInteger, "bigInteger");
  }
}
