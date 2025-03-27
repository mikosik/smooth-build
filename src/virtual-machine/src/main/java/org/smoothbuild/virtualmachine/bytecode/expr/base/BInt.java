package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigInteger;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;

/**
 * This class is thread-safe.
 */
public final class BInt extends BValue {
  public BInt(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BIntType);
  }

  public BigInteger toJavaBigInteger() throws BytecodeException {
    return readData(() -> hashedDb().readBigInteger(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("type", type())
        .addField("value", toJavaBigInteger())
        .toString();
  }
}
