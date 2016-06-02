
package com.torodb.insert.stream;

import com.torodb.core.TableRef;
import com.torodb.core.annotations.DoNotChange;
import com.torodb.core.transaction.metainf.FieldType;
import com.torodb.core.transaction.metainf.ImmutableMetaDocPart;
import com.torodb.core.transaction.metainf.ImmutableMetaField;
import com.torodb.core.transaction.metainf.MutableMetaDocPart;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 */
public class BatchMetaDocPart implements MutableMetaDocPart {

    private final MutableMetaDocPart delegate;
    private final ArrayList<ImmutableMetaField> changesOnBatch = new ArrayList<>();
    private final Consumer<BatchMetaDocPart> changeConsumer;
    private boolean createdOnCurrentBatch;

    public BatchMetaDocPart(MutableMetaDocPart delegate, Consumer<BatchMetaDocPart> changeConsumer, boolean createdOnCurrentBatch) {
        this.delegate = delegate;
        this.createdOnCurrentBatch = createdOnCurrentBatch;
        this.changeConsumer = changeConsumer;
    }

    public void newBatch() {
        changesOnBatch.clear();
        createdOnCurrentBatch = false;
    }

    public boolean isCreatedOnCurrentBatch() {
        return createdOnCurrentBatch;
    }

    public void setCreatedOnCurrentBatch(boolean createdOnCurrentBatch) {
        this.createdOnCurrentBatch = createdOnCurrentBatch;
    }

    @DoNotChange
    public Iterable<ImmutableMetaField> getOnBatchModifiedMetaFields() {
        return changesOnBatch;
    }

    @Override
    public ImmutableMetaField addMetaField(String name, String identifier, FieldType type) throws
            IllegalArgumentException {
        ImmutableMetaField newMetaField = delegate.addMetaField(name, identifier, type);

        changesOnBatch.add(newMetaField);
        changeConsumer.accept(this);

        return newMetaField;
    }

    @Override
    public ImmutableMetaField getMetaFieldByNameAndType(String fieldName, FieldType type) {
        return delegate.getMetaFieldByNameAndType(fieldName, type);
    }

    @Override
    public Stream<? extends ImmutableMetaField> streamMetaFieldByName(String fieldName) {
        return delegate.streamMetaFieldByName(fieldName);
    }

    @Override
    public ImmutableMetaField getMetaFieldByIdentifier(String fieldId) {
        return delegate.getMetaFieldByIdentifier(fieldId);
    }

    @Override
    public Stream<? extends ImmutableMetaField> streamFields() {
        return delegate.streamFields();
    }

    @Override
    public Iterable<? extends ImmutableMetaField> getAddedMetaFields() {
        return delegate.getAddedMetaFields();
    }

    @Override
    public ImmutableMetaDocPart immutableCopy() {
        return delegate.immutableCopy();
    }

    @Override
    public TableRef getTableRef() {
        return delegate.getTableRef();
    }

    @Override
    public String getIdentifier() {
        return delegate.getIdentifier();
    }
}
