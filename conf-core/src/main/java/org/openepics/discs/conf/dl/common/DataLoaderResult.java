package org.openepics.discs.conf.dl.common;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * Reports the outcome of data loading operation
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public abstract class DataLoaderResult {

    /**
     * Reports a successful data loading to the database
     */
    public static class SuccessDataLoaderResult extends DataLoaderResult {}

    /**
     * Reports a failure while loading data to the database
     */
    public static abstract class FailureDataLoaderResult extends DataLoaderResult {}

    /**
     * Reports a failure while loading data to the database because no header definition was found
     */
    public static class MissingHeaderFailureDataLoaderResult extends FailureDataLoaderResult {}

    /**
     * Reports a failure while loading data to the database because field that is required was not found
     */
    public static class RowFormatFailureDataLoaderResult extends FailureDataLoaderResult {

        private String rowNumber;
        private RowFormatFailureReason reason;

        /**
         * @param rowNumber row number in which the failure occurred
         * @param reason {@link RowFormatFailureReason}
         */
        public RowFormatFailureDataLoaderResult(String rowNumber, RowFormatFailureReason reason) {
            this.rowNumber = rowNumber;
            this.reason = reason;
        }

        /**
         * Row number where error happened
         */
        public String getRowNumber() { return this.rowNumber; }

        /**
         * Reason for failure
         */
        public RowFormatFailureReason getReason() { return this.reason; }
    }

    /**
     * Reports a failure while loading data to the database because the user does not have permissions
     * to execute some {@link EntityTypeOperation}
     */
    public static class NotAuthorizedFailureDataLoaderResult extends FailureDataLoaderResult {

        private EntityTypeOperation operation;

        /**
         * @param operation {@link EntityTypeOperation} which triggered the failure
         */
        public NotAuthorizedFailureDataLoaderResult(EntityTypeOperation operation) {
            this.operation = operation;
        }

        /**
         * {@link EntityTypeOperation} which triggered the failure
         */
        public EntityTypeOperation getOperation() { return operation; }

    }

    /**
     * Reports a failure while loading data to the database because the {@link EntityType} on which
     * operation should be executed does not exist in the database
     */
    public static class EntityNotFoundFailureDataLoaderResult extends FailureDataLoaderResult {

        private String rowNumber;
        private EntityType entity;

        /**
         * @param entity {@link EntityType} which was not found in the database
         */
        public EntityNotFoundFailureDataLoaderResult(String rowNumber, EntityType entity) {
            this.rowNumber = rowNumber;
            this.entity = entity;
        }

        /**
         * Row number where error happened
         */
        public String getRowNumber() { return this.rowNumber; }

        /**
         * {@link EntityType} which was not found in the database
         */
        public EntityType getEntity() { return entity; }

    }

    public enum RowFormatFailureReason {
        HEADER_FIELD_MISSING, COMMAND_NOT_VALID, DUPLICATE_ENTITY, RENAME_MISFORMAT, REQUIRED_FIELD_MISSING;
    }

}


