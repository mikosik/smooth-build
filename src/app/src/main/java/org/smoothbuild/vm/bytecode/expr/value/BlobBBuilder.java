package org.smoothbuild.vm.bytecode.expr.value;

import static org.smoothbuild.common.function.Function0.memoizer;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.vm.bytecode.hashed.HashingSink;

public class BlobBBuilder extends ForwardingSink {
  private final BytecodeDb bytecodeDb;
  private final Function0<BlobB, BytecodeException> hashMemoizer;

  public BlobBBuilder(BytecodeDb bytecodeDb, HashingSink hashingSink) {
    super(hashingSink);
    this.bytecodeDb = bytecodeDb;
    this.hashMemoizer = memoizer(this::createBlobB);
  }

  public BlobB build() throws BytecodeException {
    return hashMemoizer.apply();
  }

  private BlobB createBlobB() throws BytecodeException {
    HashingSink hashingSink = (HashingSink) delegate();
    try {
      hashingSink.close();
      return bytecodeDb.newBlob(hashingSink.hash());
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }
}
