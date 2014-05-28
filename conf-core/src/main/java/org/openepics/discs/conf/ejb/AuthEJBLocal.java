/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import javax.ejb.Local;

/**
 *
 * @author vuppala
 */
@Local
public interface AuthEJBLocal {
    boolean userHasAuth(String principal, String resource, char operation);
}
