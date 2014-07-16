/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.fileupload.FileUploadRenderer;

/**
 * This is a workaround to problem with p:fileupload + ajax giving 'content not 
 * in multipart/form-data exception'(from stackoverflow). 
 * 
 * @author BalusC
 */
// ToDo: remove it once primfaces fixes p:fileupload
public class MyFileUploadRenderer extends FileUploadRenderer {
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }
}
