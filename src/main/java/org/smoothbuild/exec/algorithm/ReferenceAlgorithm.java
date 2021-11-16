package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.referenceAlgorithmHash;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.lang.base.define.ModuleS;
import org.smoothbuild.lang.base.define.TopEvaluableS;
import org.smoothbuild.plugin.NativeApi;

public class ReferenceAlgorithm extends Algorithm {
  private final TopEvaluableS evaluable;
  private final ModuleS module;

  public ReferenceAlgorithm(TopEvaluableS evaluable, ModuleS module, TupleTypeH type) {
    super(type);
    this.evaluable = evaluable;
    this.module = module;
  }

  @Override
  public Hash hash() {
    return referenceAlgorithmHash(module.hash(), evaluable.name());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    StringH name = nativeApi.factory().string(evaluable.name());
    BlobH moduleHash = nativeApi.factory().blob(sink -> sink.write(module.hash().toByteString()));
    TupleH functionTuple = nativeApi
        .factory()
        .tuple((TupleTypeH) outputType(), list(name, moduleHash));
    return new Output(functionTuple, nativeApi.messages());
  }
}
