package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static org.smoothbuild.common.function.Function0.memoizer;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashingSink;

public class BBlobBuilder extends ForwardingSink {
  private final BExprDb exprDb;
  private final Function0<BBlob, IOException> hashMemoizer;

  public BBlobBuilder(BExprDb exprDb, HashingSink hashingSink) {
    super(hashingSink);
    this.exprDb = exprDb;
    this.hashMemoizer = memoizer(this::createBlobB);
  }

  public BBlob build() throws IOException {
    return hashMemoizer.apply();
  }

  private BBlob createBlobB() throws IOException {
    HashingSink hashingSink = (HashingSink) delegate();
    hashingSink.close();
    return exprDb.newBlob(hashingSink.hash());
  }
}
