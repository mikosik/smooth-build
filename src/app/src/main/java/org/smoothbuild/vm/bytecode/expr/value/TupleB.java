package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Suppliers.memoize;
import static java.util.Objects.checkIndex;
import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.bytecode.type.Validator.validateTuple;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public final class TupleB extends ValueB {
  private final Supplier<ImmutableList<ValueB>> elementsSupplier;

  public TupleB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    this.elementsSupplier = memoize(this::instantiateItems);
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
    ImmutableList<ValueB> elements = elements();
    checkIndex(index, elements.size());
    return elements.get(index);
  }

  public ImmutableList<ValueB> elements() {
    return elementsSupplier.get();
  }

  private ImmutableList<ValueB> instantiateItems() {
    var type = type();
    var expectedElementTs = type.elements();
    var elements = readDataSeqElems(expectedElementTs.size());
    var elementTs = map(elements, ValueB::type);
    validateTuple(type, elementTs, () -> {throw new DecodeExprWrongNodeTypeExc(hash(),
        category(), DATA_PATH, type, asTupleToString(elementTs));});
    return elements;
  }

  private static String asTupleToString(ImmutableList<TypeB> elementTs) {
    return "`{" + joinWithCommaToString(elementTs) + "}`";
  }

  @Override
  public String exprToString() {
    return "{" + exprsToString(elements()) + '}';
  }
}
