package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.join;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;
import static org.smoothbuild.util.Lists.zip;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.spec.base.ValSpec;

import com.google.common.collect.ImmutableList;

public class StructSpec extends ValSpec {
  private final RecSpec items;
  private final ImmutableList<String> names;
  private final String name;

  public StructSpec(Hash hash, RecSpec items, ImmutableList<String> names) {
    super(hash, STRUCT);
    this.items = items;
    this.names = names;
    this.name = calculateName(items, names);
  }

  private String calculateName(RecSpec items, ImmutableList<String> names) {
    return join(", ", zip(items.items(), names, (i, n) -> i.name() + " " + n));
  }

  public RecSpec items() {
    return items;
  }

  public ImmutableList<String> names() {
    return names;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Lambda newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Lambda(merkleRoot, objectDb);
  }
}
