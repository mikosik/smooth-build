package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.base.Strings.escaped;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;

/**
 * This class is thread-safe.
 */
public final class BString extends BValue {
  public BString(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BStringType);
  }

  public String toJavaString() throws BytecodeException {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("type", type())
        .addField("value", "\"" + escaped(toJavaString()) + "\"")
        .toString();
  }
}
