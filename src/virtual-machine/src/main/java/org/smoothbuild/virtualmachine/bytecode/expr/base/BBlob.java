package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static okio.Okio.buffer;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainIOException;

import okio.Source;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;

/**
 * This class is thread-safe.
 */
public final class BBlob extends BValue {
  public BBlob(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BBlobType);
  }

  public Source source() throws BytecodeException {
    return readData(() -> hashedDb().source(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("value", toHexString())
        .toString();
  }

  private String toHexString() throws BytecodeException {
    return invokeAndChainIOException(
        () -> "0x" + buffer(source()).readByteString().hex(), BytecodeException::new);
  }
}
