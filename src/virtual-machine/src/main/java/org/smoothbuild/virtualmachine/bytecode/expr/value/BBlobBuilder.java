package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static org.smoothbuild.common.function.Function0.memoizer;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashingSink;

public class BBlobBuilder extends ForwardingSink {
  private final BExprDb exprDb;
  private final Function0<BBlob, BytecodeException> hashMemoizer;

  public BBlobBuilder(BExprDb exprDb, HashingSink hashingSink) {
    super(hashingSink);
    this.exprDb = exprDb;
    this.hashMemoizer = memoizer(this::createBlobB);
  }

  public BBlob build() throws BytecodeException {
    return hashMemoizer.apply();
  }

  private BBlob createBlobB() throws BytecodeException {
    HashingSink hashingSink = (HashingSink) delegate();
    try {
      hashingSink.close();
      return exprDb.newBlob(hashingSink.hash());
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }
}
