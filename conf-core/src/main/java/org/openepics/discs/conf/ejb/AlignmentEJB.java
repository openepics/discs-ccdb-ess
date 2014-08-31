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

        defineParentChildInterface(AlignmentPropertyValue.class, new ParentChildInterface<AlignmentRecord, AlignmentPropertyValue>() {
            @Override
            public List<AlignmentPropertyValue> getChildCollection(AlignmentRecord type) {
                return type.getAlignmentPropertyList();
            }
            @Override
            public AlignmentRecord getParentFromChild(AlignmentPropertyValue child) {
                return child.getAlignmentRecord();
            }
        });

        defineParentChildInterface(AlignmentArtifact.class, new ParentChildInterface<AlignmentRecord, AlignmentArtifact>() {
            @Override
            public List<AlignmentArtifact> getChildCollection(AlignmentRecord type) {
                return type.getAlignmentArtifactList();
            }
            @Override
            public AlignmentRecord getParentFromChild(AlignmentArtifact child) {
                return child.getAlignmentRecord();
            }
        });
    }
}
