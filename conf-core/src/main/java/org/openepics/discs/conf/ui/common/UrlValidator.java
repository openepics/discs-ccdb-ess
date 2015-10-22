package org.openepics.discs.conf.ui.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;

@FacesValidator("custom.urlValidator")
public class UrlValidator implements Validator, ClientValidator {

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.urlValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(value == null) {
            return;
        }

        try {
            URL theUrl = new URL(value.toString());

            List<String> acceptedProtocols = Arrays.asList("https", "http", "ftp");
            if (!acceptedProtocols.contains(theUrl.getProtocol()))
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                            "URL with this protocol is not accepted."));
        } catch (MalformedURLException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    "This is not a valid URL."), e);
        }
    }

}

