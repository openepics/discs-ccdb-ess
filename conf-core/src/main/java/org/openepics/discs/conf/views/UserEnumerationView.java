package org.openepics.discs.conf.views;

import java.util.List;

import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.util.Conversion;

import com.google.common.base.Preconditions;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class UserEnumerationView {
    private final String name;
    private final String description;
    private final List<String> definition;
    private final String definitionString;
    private final DataType enumeration;

    public UserEnumerationView(DataType enumeration) {
        Preconditions.checkArgument(!Preconditions.checkNotNull(enumeration).isScalar());

        name = enumeration.getName();
        description = enumeration.getDescription();
        definition = Conversion.prepareEnumSelections(enumeration);
        definitionString = buildDefString();
        this.enumeration = enumeration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDefinition() {
        return definition;
    }

    public String getDefinitionAsString() {
        return definitionString;
    }

    public DataType getEnumeration() {
        return enumeration;
    }

    private String buildDefString() {
        final StringBuilder defString = new StringBuilder();
        defString.append('[');

        boolean first = true;
        for (String def : definition) {
            if (!first) {
                defString.append(", ");
            } else {
                first = false;
            }
            defString.append(def);
        }

        defString.append(']');
        return defString.toString();
    }
}
