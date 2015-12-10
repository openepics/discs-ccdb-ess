package org.openepics.discs.conf.views;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.conf.ent.NamedEntity;
import org.openepics.discs.conf.ent.Tag;

public class EntityAttrTagView<E extends NamedEntity> extends EntityAttributeView<E> {
    private Tag entity;

    public EntityAttrTagView(Tag entity, EntityAttributeViewKind kind, E parent, String usedBy) {
        super(kind, parent, usedBy);
        this.entity = entity;
    }

    public EntityAttrTagView(Tag entity, EntityAttributeViewKind kind, E parent) {
        this(entity, kind, parent, null);
    }

    public EntityAttrTagView(E parent) {
        super(null, parent);
        entity = new Tag();
    }


    /** @return The value of the tag */
    @NotNull
    @Size(min = 1, max = 255, message="Tag can have at most 255 characters.")
    public String getTag() {
        return entity.getName();
    }

    /** Called by the UI input control to set the value.
     * @param tag The value of the tag
     */
    public void setTag(String tag) {
        entity.setName(tag);
    }

    @Override
    public Tag getEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return "TAG_" + entity.getName();
    }

    @Override
    public String getName() {
        return  entity.getName();
    }

    @Override
    public String getValue() {
        return null;
    }

}
