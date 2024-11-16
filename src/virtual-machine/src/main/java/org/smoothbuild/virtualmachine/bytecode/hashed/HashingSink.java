package org.smoothbuild.virtualmachine.bytecode.hashed;

import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb.dbPathTo;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.Bucket;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.function.Function0;

public class HashingSink extends ForwardingSink {
  private final Bucket bucket;
  private final Path tempPath;
  private final Function0<Hash, IOException> hashMemoizer;

  HashingSink(Bucket bucket, Path tempPath) throws IOException {
    super(Hash.hashingSink(bucket.sink(tempPath)));
    this.bucket = bucket;
    this.tempPath = tempPath;
    this.hashMemoizer = memoizer(this::calculateHash);
  }

  public Hash hash() throws IOException {
    return hashMemoizer.apply();
  }

  @Override
  public void close() throws IOException {
    hashMemoizer.apply();
  }

  private Hash calculateHash() throws IOException {
    var hashingSink = (okio.HashingSink) delegate();
    hashingSink.close();
    var localHash = new Hash(hashingSink.hash());
    var path = dbPathTo(localHash);
    var pathState = bucket.pathState(path);
    switch (pathState) {
      case NOTHING -> bucket.move(tempPath, path);
      case FILE -> bucket.delete(tempPath);
      case DIR -> throw new IOException(
          "Corrupted HashedDb. Cannot store data at " + path.q() + " as it is a directory.");
    }
    return localHash;
  }
}
