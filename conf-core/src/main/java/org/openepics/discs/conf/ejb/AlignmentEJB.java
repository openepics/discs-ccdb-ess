/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentPropertyValue;
import org.openepics.discs.conf.ent.AlignmentRecord;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class AlignmentEJB extends DAO<AlignmentRecord> {
    @Override
    protected void defineEntity() {
        defineEntityClass(AlignmentRecord.class);

        defineParentChildInterface(AlignmentPropertyValue.class,
                new ParentChildInterface<AlignmentRecord, AlignmentPropertyValue>() {
            @Override
            public List<AlignmentPropertyValue> getChildCollection(AlignmentRecord record) {
                return record.getAlignmentPropertyList();
            }
            @Override
            public AlignmentRecord getParentFromChild(AlignmentPropertyValue child) {
                return child.getAlignmentRecord();
            }
        });

        defineParentChildInterface(AlignmentArtifact.class,
                new ParentChildInterface<AlignmentRecord, AlignmentArtifact>() {
            @Override
            public List<AlignmentArtifact> getChildCollection(AlignmentRecord record) {
                return record.getAlignmentArtifactList();
            }
            @Override
            public AlignmentRecord getParentFromChild(AlignmentArtifact child) {
                return child.getAlignmentRecord();
            }
        });
    }
}
