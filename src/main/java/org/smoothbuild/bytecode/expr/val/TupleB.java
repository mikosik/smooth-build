package org.smoothbuild.bytecode.expr.val;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;
import static org.smoothbuild.bytecode.type.Validator.validateTuple;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValB {
  private final Supplier<ImmutableList<ValB>> itemsSupplier;

  public TupleB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.itemsSupplier = memoize(this::instantiateItems);
  }

  @Override
  public TupleTB type() {
    return (TupleTB) super.category();
  }

  public ValB get(int index) {
    ImmutableList<ValB> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ImmutableList<ValB> items() {
    return itemsSupplier.get();
  }

  private ImmutableList<ValB> instantiateItems() {
    var type = type();
    var expectedItemTs = type.items();
    var items = readSeqExprs(DATA_PATH, dataHash(), expectedItemTs.size(), ValB.class);
    var itemTs = map(items, ValB::type);
    validateTuple(type, itemTs, () -> {throw new DecodeExprWrongNodeTypeExc(hash(),
        category(), DATA_PATH, type, asTupleToString(itemTs));});
    return items;
  }

  private static String asTupleToString(ImmutableList<TypeB> ItemTs) {
    return "`{" + toCommaSeparatedString(ItemTs) + "}`";
  }

  @Override
  public String exprToString() {
    return "{" + exprsToString(items()) + '}';
  }
}
