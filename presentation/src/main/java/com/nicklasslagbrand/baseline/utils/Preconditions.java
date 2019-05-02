package com.nicklasslagbrand.baseline.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;

/**
 * Static convenience methods that help a method or constructor check whether it was invoked
 * correctly (whether its <i>preconditions</i> have been met).
 */
public final class Preconditions {

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param referenceName the reference name to use if the check fails; will be formatted into
     * "'referenceName' should not be null" string.
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(@Nullable T reference, @Nullable Object referenceName) {
        if (reference == null) {
            throw new NullPointerException("'" + referenceName + "' should not be null");
        }
        return reference;
    }

    /**
     * Ensures that provided text is noy null or empty.
     *
     * @param text {@link String} to be checked.
     * @param variableName name of the {@link String} that will used if check fails.
     * @throws IllegalStateException if check fails.
     */
    public static void checkNotNullOrEmpty(@Nullable String text, String variableName) {
        if (text == null || text.isEmpty()) {
            throw new IllegalStateException("'" + variableName + "' should not be null or empty");
        }
    }

    /**
     * Ensures that provided list is not empty.
     *
     * @param items {@link Collection} that should be checked.
     * @param variableName name of the {@link Collection} that will used if check fails.
     * @throws IllegalStateException if check fails.
     */
    public static <T> void checkNotEmpty(@NonNull Collection<T> items, String variableName) {
        if (items.isEmpty()) {
            throw new IllegalStateException("'" + variableName + "' should not be empty");
        }
    }
}

