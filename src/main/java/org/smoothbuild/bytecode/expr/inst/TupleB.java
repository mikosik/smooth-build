package org.smoothbuild.bytecode.expr.inst;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;
import static org.smoothbuild.bytecode.type.Validator.validateTuple;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValueB {
  private final Supplier<ImmutableList<ValueB>> itemsSupplier;

  public TupleB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.itemsSupplier = memoize(this::instantiateItems);
  }

  @Override
  public TupleTB evalT() {
    return type();
  }

  @Override
  public TupleTB type() {
    return (TupleTB) super.category();
  }

  public ValueB get(int index) {
    ImmutableList<ValueB> items = items();
    checkIndex(index, items.size());
    return items.get(index);
  }

  public ImmutableList<ValueB> items() {
    return itemsSupplier.get();
  }

  private ImmutableList<ValueB> instantiateItems() {
    var type = type();
    var expectedItemTs = type.items();
    var items = readDataSeqElems(expectedItemTs.size());
    var itemTs = map(items, ValueB::type);
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
