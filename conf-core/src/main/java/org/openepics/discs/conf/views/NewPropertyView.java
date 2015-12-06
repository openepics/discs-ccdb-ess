package org.openepics.discs.conf.views;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.openepics.discs.conf.util.BatchIterator;
import org.openepics.discs.conf.util.BatchSaveStage;
import org.openepics.discs.conf.util.Utility;

public class NewPropertyView extends PropertyView implements Iterable<String> {
    // ---- batch property creation
    private boolean isBatchCreation;
    private int batchStartIndex;
    private int batchEndIndex;
    private int batchLeadingZeros;
    private String batchPropertyConflicts;
    private BatchSaveStage batchSaveStage;

    public NewPropertyView()
    {
        setBatchSaveStage(BatchSaveStage.VALIDATION);
    }

    /** @return the isBatchCreation */
    public boolean isBatchCreation() {
        return isBatchCreation;
    }

    /** @param isBatchCreation the isBatchCreation to set */
    public void setBatchCreation(boolean isBatchCreation) {
        this.isBatchCreation = isBatchCreation;
    }

    /** @return the batchStartIndex */
    public int getBatchStartIndex() {
        return batchStartIndex;
    }
    /** @param batchStartIndex the batchStartIndex to set */
    public void setBatchStartIndex(int batchStartIndex) {
        this.batchStartIndex = batchStartIndex;
    }

    /** @return the batchEndIndex */
    public int getBatchEndIndex() {
        return batchEndIndex;
    }
    /** @param batchEndIndex the batchEndIndex to set */
    public void setBatchEndIndex(int batchEndIndex) {
        this.batchEndIndex = batchEndIndex;
    }

    /** @return the batchLeadingZeros */
    public int getBatchLeadingZeros() {
        return batchLeadingZeros;
    }
    /** @param batchLeadingZeros the batchLeadingZeros to set */
    public void setBatchLeadingZeros(int batchLeadingZeros) {
        this.batchLeadingZeros = batchLeadingZeros;
    }

    /** The validator for the end index field
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
     */
    public void batchEndValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (batchStartIndex >= (Integer)value) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "End index must be greater than start index."));
        }
    }

    /** The validator for the start index field
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
     */
    public void batchStartValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if ((Integer)value >= batchEndIndex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "Start index must be less than end index."));
        }
    }

    /** @return a new line separated list of all properties in conflict */
    public String getBatchPropertyConflicts() {
        return batchPropertyConflicts;
    }

    @Override
    public Iterator<String> iterator() {
        return new BatchIterator(batchStartIndex, batchEndIndex, batchLeadingZeros);
    }

    /** @return the batchSaveStage */
    public BatchSaveStage getBatchSaveStage() {
        return batchSaveStage;
    }

    /** @param batchSaveStage the batchSaveStage to set */
    public void setBatchSaveStage(BatchSaveStage batchSaveStage) {
        this.batchSaveStage = batchSaveStage;
    }

    public void setBatchPropertyConflicts(String batchPropertyConflicts) {
        this.batchPropertyConflicts = batchPropertyConflicts;
    }

    /** @see org.openepics.discs.conf.views.PropertyView#nameValidator(java.lang.String) */
    @Override
    public void nameValidator(String propertyName) {
        if (isBatchCreation) {
            if (!propertyName.contains("{i}")) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "Batch creation selected, but index position \"{i}\" not set"));
            }
        } else {
            super.nameValidator(propertyName);
        }
    }
}
