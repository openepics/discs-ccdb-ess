package org.openepics.discs.conf.ent.values;

import org.junit.Test;

public class ValueInitializationTest {

    @Test(expected = NullPointerException.class)
    public void intValue() {
        IntValue intValue = new IntValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void dblValue() {
        DblValue dblValue = new DblValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void strValue() {
        StrValue strValue = new StrValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void timestampValue() {
        TimestampValue timestampValue = new TimestampValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void urlValue() {
        UrlValue urlValue = new UrlValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void enumValue() {
        EnumValue enumValue = new EnumValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void intVectorValue() {
        IntVectorValue intVectorValue = new IntVectorValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void dblVectorValue() {
        DblVectorValue dblVectorValue = new DblVectorValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void strVectorValue() {
        StrVectorValue strVectorValue = new StrVectorValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void dblTableValue() {
        DblTableValue dblTableValue = new DblTableValue(null);
    }
}
