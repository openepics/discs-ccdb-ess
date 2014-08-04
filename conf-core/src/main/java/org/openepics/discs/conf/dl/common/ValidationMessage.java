package org.openepics.discs.conf.dl.common;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * This holds one piece of validation information tied to a location.
 *
 * @author Sunil Sah <sunil.sah@cosylab.com>
 */
public class ValidationMessage {

	private ErrorMessage message;

	private boolean error;

	private String row;
	private String column;
	private EntityTypeOperation operation;
	private EntityType entity;
	private String fileName;
	private String orphanSlotName;

	public ValidationMessage(String fileName) {
	    this.fileName = fileName;
	    error = false;
	}

	/**
     * Constructs the message.
     *
     * @param message the message enumeration
     */
    public ValidationMessage(ErrorMessage message) {
        this(message, null);
    }

	/**
	 * Constructs the message.
	 *
	 * @param message the message enumeration
     * @param row the row location description, or null for no information
	 */
	public ValidationMessage(ErrorMessage message, String row) {
		this(message, row, null);
	}


	/**
	 * Constructs the message with location information.
	 *
	 * @param message the message enumeration
	 * @param row the row location description, or null for no information
	 * @param column the column location description, or null for no information
	 */
	public ValidationMessage(ErrorMessage message, String row,
			String column) {
		this(message, row, column, null, null);
	}

	/**
	 * Construct the message with location information and information of {@link EntityType} and {@link EntityTypeOperation}
	 *
	 * @param message the message enumeration
	 * @param row
	 * @param column
	 * @param operation
	 * @param entity
	 */
	public ValidationMessage(ErrorMessage message, String row,
            String column, EntityTypeOperation operation, EntityType entity) {
        super();
        this.message = message;
        this.error = true;
        this.row = row;
        this.column = column;
        this.operation = operation;
        this.entity = entity;
    }

	public void setOrphanSlotName(String orphanSlotName) { this.orphanSlotName = orphanSlotName; }

	/** @return the message enum */
	public ErrorMessage getMessage() { return message; }

	/** @return true if this message represents an error */
	public boolean isError() { return error; }

	/** @return the row label, or null if not specified */
	public String getRow() { return row; }

	/** @return the column label, or null if not specified */
	public String getColumn() { return column; }

	/** @return operation on entity */
	public EntityTypeOperation getOperation() { return operation; }

	/** @return entity */
	public EntityType getEntity() { return entity; }

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (fileName != null) {
		    builder.append("In file: " + fileName);
		} else {
		    if (orphanSlotName != null) {
		        builder.append("Found orphan slot ");
		        builder.append(orphanSlotName);
		        builder.append(", ");
		    }
    		if (getRow() != null) {
    			builder.append("Row ");
    			builder.append(getRow());
    		}
    		if (getRow() != null && getColumn() != null) {
    			builder.append(", ");
    		}
    		if (getColumn() != null) {
    			builder.append("Column ");
    			builder.append(getColumn());
    		}
    		if ((getRow() != null || getColumn() != null) && (getEntity() != null || getOperation() != null)) {
                builder.append(", ");
            }
    		if (getEntity() != null) {
                builder.append("Entity ");
                builder.append(getEntity().name());
            }
    		if ((getRow() != null || getColumn() != null || getEntity() != null) && getOperation() != null) {
                builder.append(", ");
            }
    		if (getOperation() != null) {
                builder.append("Operation ");
                builder.append(getOperation().name());
            }
    		if (getRow() != null || getColumn() != null || getEntity() != null || getOperation() != null) {
    			builder.append(": ");
    		}
    		if (isError()) {
    			builder.append("ERROR: ");
    		}
    		builder.append(getMessage().toString());
		}
		return builder.toString();
	}
}
