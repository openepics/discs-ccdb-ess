package org.openepics.discs.conf.util;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

/**
 * A static utility class for casting @Nullable values to non-@Nullable.
 *
 * @author Marko Kolar <marko.kolar@cosylab.com>
 */
public class As {
    /**
     * The cast of the value declared nullable to the same type that does not permit null values. Throws an exception if
     * the input value is, in fact, null.
     */
    public static <T> T notNull(@Nullable T value) {
        Preconditions.checkNotNull(value);
        return value;
    }
}
