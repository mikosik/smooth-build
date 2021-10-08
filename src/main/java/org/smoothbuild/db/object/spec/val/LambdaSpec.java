package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.db.object.spec.base.SpecKind.LAMBDA;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.FunctionType;

import com.google.common.collect.ImmutableList;

public class LambdaSpec extends ValSpec implements FunctionType {
  private final ValSpec result;
  private final RecSpec parametersRec;

  public LambdaSpec(Hash hash, ValSpec result, RecSpec parametersRec) {
    super(hash, LAMBDA);
    this.result = result;
    this.parametersRec = parametersRec;
  }

  @Override
  public ValSpec result() {
    return result;
  }

  @Override
  public ImmutableList<ValSpec> parameters() {
    return parametersRec.items();
  }

  public RecSpec parametersRec() {
    return parametersRec;
  }

  @Override
  public String name() {
    String map = parametersRec.items()
        .stream()
        .map(Spec::name)
        .collect(joining(","));
    return result.name() + "(" + map + ")";
  }

  @Override
  public Lambda newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Lambda(merkleRoot, objectDb);
  }
}
