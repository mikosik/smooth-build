package org.smoothbuild.compile.ps.ast.expr;

import static java.lang.Character.isDigit;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;

import java.math.BigInteger;

import org.smoothbuild.compile.lang.base.Loc;

public final class IntP extends ValP {
  private final String literal;
  private BigInteger bigInteger;

  public IntP(String literal, Loc loc) {
    super(INT, loc);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  public void decodeBigInteger() throws NumberFormatException {
    assertNoLeadingZeros(literal);
    assertNotNegativeZero(literal);
    bigInteger = new BigInteger(literal, 10);
  }

  private static void assertNoLeadingZeros(String literal) {
    int index = findFirstDigit(literal);
    if (index != -1
        && literal.charAt(index) == '0'
        && index + 1 < literal.length()
        && isDigit(literal.charAt(index + 1))) {
      throw new NumberFormatException();
    }
  }

  private static int findFirstDigit(String literal) {
    for (int i = 0; i < literal.length(); i++) {
      if (isDigit(literal.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  private static void assertNotNegativeZero(String literal) {
    if (literal.equals("-0")) {
      throw new NumberFormatException();
    }
  }

  public BigInteger bigInteger() {
    return bigInteger;
  }
}
