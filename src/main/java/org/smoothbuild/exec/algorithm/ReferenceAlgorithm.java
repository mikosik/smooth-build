package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.referenceAlgorithmHash;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.SModule;
import org.smoothbuild.plugin.NativeApi;

public class ReferenceAlgorithm extends Algorithm {
  private final GlobalReferencable referencable;
  private final SModule module;

  public ReferenceAlgorithm(GlobalReferencable referencable, SModule module, TupleSpec spec) {
    super(spec);
    this.referencable = referencable;
    this.module = module;
  }

  @Override
  public Hash hash() {
    return referenceAlgorithmHash(module.hash(), referencable.name());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Str name = nativeApi.factory().string(referencable.name());
    Blob moduleHash = nativeApi.factory().blob(sink -> sink.write(module.hash().toByteString()));
    Tuple functionTuple = nativeApi
        .factory()
        .tuple((TupleSpec) outputSpec(), list(name, moduleHash));
    return new Output(functionTuple, nativeApi.messages());
  }
}
