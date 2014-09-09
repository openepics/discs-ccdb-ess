package org.openepics.discs.conf.sedsconverters;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.core.datatypes.SimpleSedsFactory;
import org.openepics.seds.util.ScalarType;

@FacesConverter("seds_int_converter")
public class IntegerConverter  implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            final int intValue = Integer.parseInt(value);
            return new SimpleSedsFactory().newScalar(intValue);
        } catch (NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot parse the value into integer", null);
            throw new ConverterException(msg);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) return "";

        if (!(value instanceof SedsScalar<?>) || (((SedsScalar<?>)value).getType() != ScalarType.INTEGER)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Expected integer value", null);
            throw new ConverterException(msg);
        }

        final SedsScalar<Integer> intScalar = (SedsScalar<Integer>) value;
        return intScalar.getValue().toString();
    }

}
