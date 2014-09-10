package org.openepics.discs.conf.security;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

import org.openepics.discs.conf.ent.EntityTypeOperation;

@SessionScoped
@Named("securityPolicy")
@Alternative
public class DummySecurityPolicy implements SecurityPolicy, Serializable {
    private static final Logger logger = Logger.getLogger(DummySecurityPolicy.class.getCanonicalName());

    public DummySecurityPolicy() {
        logger.log(Level.INFO, "Creating " + this.getClass().getCanonicalName());
    }

    @Override
    public String getUserId() {
        return "admin";
    }

    @Override
    public void login(String userName, String password) {
    }

    @Override
    public void logout() {
    }

    @Override
    public void checkAuth(Object entity, EntityTypeOperation operationType) {
    }

    @Override
    public boolean getUIHint(String param) {
        return true;
    }

}
