package org.smoothbuild.parse.ast;

import static java.lang.Character.isDigit;

import java.math.BigInteger;

import org.smoothbuild.lang.define.Loc;

public final class IntN extends ExprN {
  private final String literal;
  private BigInteger bigInteger;

  public IntN(String literal, Loc loc) {
    super(loc);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  public void decodeBigInteger() throws NumberFormatException {
    assertNoLeadingZeros(this.literal);
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
