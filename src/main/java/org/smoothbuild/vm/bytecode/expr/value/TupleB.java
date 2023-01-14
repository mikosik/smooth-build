package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;
import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.type.Validator;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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
  public TupleTB evaluationT() {
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
    Validator.validateTuple(type, itemTs, () -> {throw new DecodeExprWrongNodeTypeExc(hash(),
        category(), DATA_PATH, type, asTupleToString(itemTs));});
    return items;
  }

  private static String asTupleToString(ImmutableList<TypeB> ItemTs) {
    return "`{" + joinWithCommaToString(ItemTs) + "}`";
  }

  @Override
  public String exprToString() {
    return "{" + exprsToString(items()) + '}';
  }
}
