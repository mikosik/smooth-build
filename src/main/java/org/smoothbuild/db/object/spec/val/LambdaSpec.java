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

public class LambdaSpec extends ValSpec {
  private final ValSpec result;
  private final RecSpec parameters;
  private final RecSpec defaultArguments;

  public LambdaSpec(Hash hash, ValSpec result, RecSpec parameters, RecSpec defaultArguments) {
    super(hash, LAMBDA);
    this.result = result;
    this.parameters = parameters;
    this.defaultArguments = defaultArguments;
  }

  public ValSpec result() {
    return result;
  }

  public RecSpec parameters() {
    return parameters;
  }

  public RecSpec defaultArguments() {
    return defaultArguments;
  }

  @Override
  public String name() {
    String map = parameters.items()
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
