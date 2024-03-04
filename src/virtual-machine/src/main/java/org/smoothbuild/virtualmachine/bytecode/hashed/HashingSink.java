package org.smoothbuild.virtualmachine.bytecode.hashed;

import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb.dbPathTo;

import java.io.IOException;
import okio.ForwardingSink;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.function.Function0;

public class HashingSink extends ForwardingSink {
  private final FileSystem fileSystem;
  private final Path tempPath;
  private final Function0<Hash, IOException> hashMemoizer;

  HashingSink(FileSystem fileSystem, Path tempPath) throws IOException {
    super(Hash.hashingSink(fileSystem.sinkWithoutBuffer(tempPath)));
    this.fileSystem = fileSystem;
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
    var pathState = fileSystem.pathState(path);
    switch (pathState) {
      case NOTHING -> fileSystem.move(tempPath, path);
      case FILE -> fileSystem.delete(tempPath);
      case DIR -> throw new IOException(
          "Corrupted HashedDb. Cannot store data at " + path.q() + " as it is a directory.");
    }
    return localHash;
  }
}
