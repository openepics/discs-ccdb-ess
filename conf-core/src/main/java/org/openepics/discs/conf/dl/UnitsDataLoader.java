package org.openepics.discs.conf.dl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.IllegalImportFileFormatException;
import org.openepics.discs.conf.util.NotAuthorizedException;

@Stateless
public class UnitsDataLoader extends DataLoader {

	@Inject	private LoginManager loginManager;
	@Inject	private AuthEJB authEJB;
	@Inject	private ConfigurationEJB configurationEJB;
	@PersistenceContext	private EntityManager em;
	private HashMap<String, Unit> unitById;
	private List<String> headerFields;
	private int idIndex, quantityIndex, symbolIndex, baseUnitExprIndex, descriptionIndex;
	private List<Unit> units;

	public void loadData(InputStream stream) throws IllegalImportFileFormatException, NotAuthorizedException {
		init();

		final List<List<String>> inputRows = ExcelImportFileReader.importExcelFile(stream);

		if (inputRows != null && inputRows.size() > 0) {
			/*
			 * List does not contain any rows that do not have a value (command)
			 * in the first column. There should be no commands before "HEADER".
			 */
			List<String> headerRow = inputRows.get(0);
			setUpIndexesForFields(headerRow);
			CommandProcessing: for (List<String> row : inputRows.subList(1, inputRows.size())) {
				final String rowNumber = row.get(0);
				if (row.get(1).equals(CMD_HEADER)) {
					headerRow = row;
					setUpIndexesForFields(headerRow);
					continue; // skip the rest of the processing for HEADER row
				}
				// TODO correctly notify the user, that one of the required
				// entity fields is missing a value

				final String command = row.get(1).toUpperCase();
				final String id = row.get(idIndex);
				final String quantity = row.get(quantityIndex);
				final String symbol = row.get(symbolIndex);
				final String description = row.get(descriptionIndex);
				final String baseUnitExpr = row.get(baseUnitExprIndex);
				final Date modifiedAt = new Date();
				final String modifiedBy = loginManager.getUserid();

				switch (command) {
				case CMD_UPDATE:
					if (unitById.containsKey(id)) {
						if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.UPDATE)) {
							final Unit unitToUpdate = unitById.get(id);
							unitToUpdate.setBaseUnitExpr(baseUnitExpr);
							unitToUpdate.setDescription(description);
							unitToUpdate.setModifiedAt(modifiedAt);
							unitToUpdate.setQuantity(quantity);
							unitToUpdate.setSymbol(symbol);
							unitToUpdate.setModifiedBy(modifiedBy);
						} else {
							throw new NotAuthorizedException(EntityTypeOperation.UPDATE, EntityType.UNIT);
						}
					} else {
						if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.CREATE)) {
							em.persist(new Unit(id, quantity, symbol, baseUnitExpr, description, modifiedAt, loginManager
									.getUserid(), 0));
						} else {
							throw new NotAuthorizedException(EntityTypeOperation.CREATE, EntityType.UNIT);
						}
					}
					break;
				case CMD_DELETE:
					if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.DELETE)) {
						final Unit unitToDelete = unitById.get(id);
						if (unitToDelete == null) {
							throw new IllegalImportFileFormatException("Unit to be deleted does not exist!", rowNumber);
						} else {
							em.remove(em.contains(unitToDelete) ? unitToDelete : em.merge(unitToDelete));
						}
					} else {
						throw new NotAuthorizedException(EntityTypeOperation.DELETE, EntityType.UNIT);
					}
					break;
				case CMD_END:
					break CommandProcessing;
				case CMD_RENAME:
				default:
					throw new IllegalImportFileFormatException(command + " is not a valid command!", rowNumber);
				}
			}
		}
	}

	private void init() {
		units = configurationEJB.findUnits();
		unitById = new HashMap<>();
		for (Unit unit : units) {
			unitById.put(unit.getUnitId(), unit);
		}

		headerFields = new ArrayList<>();
		headerFields.add("ID");
		headerFields.add("QUANTITY");
		headerFields.add("SYMBOL");
		headerFields.add("EXPR");
		headerFields.add("DESCRIPTION");
	}

	@Override
	protected void setUpIndexesForFields(List<String> header) throws IllegalImportFileFormatException {
		idIndex = header.indexOf("ID");
		quantityIndex = header.indexOf("QUANTITY");
		symbolIndex = header.indexOf("SYMBOL");
		baseUnitExprIndex = header.indexOf("EXPR");
		descriptionIndex = header.indexOf("DESCRIPTION");

		if (idIndex == -1 || quantityIndex == -1 || symbolIndex == -1 || baseUnitExprIndex == -1 || descriptionIndex == -1) {
			throw new IllegalImportFileFormatException("Header row does not contain required fields!", header.get(0));
		}
	}
}
