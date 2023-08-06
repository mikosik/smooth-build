package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static org.smoothbuild.common.function.Function0.memoizer;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashingSink;

public class BlobBBuilder extends ForwardingSink {
  private final ExprDb exprDb;
  private final Function0<BlobB, BytecodeException> hashMemoizer;

  public BlobBBuilder(ExprDb exprDb, HashingSink hashingSink) {
    super(hashingSink);
    this.exprDb = exprDb;
    this.hashMemoizer = memoizer(this::createBlobB);
  }

  public BlobB build() throws BytecodeException {
    return hashMemoizer.apply();
  }

  private BlobB createBlobB() throws BytecodeException {
    HashingSink hashingSink = (HashingSink) delegate();
    try {
      hashingSink.close();
      return exprDb.newBlob(hashingSink.hash());
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }
}
