/*
 * This file is part of MinigameCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 MinigameCore <http://minigamecore.github.io>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.minigamecore.plugin.util.reflect;

import com.google.inject.Singleton;
import io.github.minigamecore.plugin.util.logger.MinigameCoreLogger;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

/**
 * Reflection helper class.
 */
@SuppressWarnings("WeakerAccess")
@Singleton
public final class Reflection {

    private final Logger logger = new MinigameCoreLogger("reflection");
    private Field fieldModifierField;

    public Reflection() throws NoSuchFieldException {
        // Field#modifiers
        fieldModifierField = Field.class.getDeclaredField("modifiers");
        fieldModifierField.setAccessible(true);
    }

    public boolean isAccessible(Field field) {
        return field.isAccessible();
    }

    public boolean isModifierPresent(Field field, int modifier) {
        return (modifier % 2 != 0 && modifier != 1) && field.getModifiers() == (field.getModifiers() & modifier);
    }

    public void setAccessible(Field field, boolean accessible) {
        boolean a = field.isAccessible();
        field.setAccessible(accessible);
        logger.debug("For field {}, previous accessible value {}, new accessible value {}", field, a, accessible);
    }

    public void setModifier(Field field, int modifier) throws IllegalAccessException {
        if (!(modifier % 2 != 0 && modifier != 1)) {
            logger.debug("{} is not a valid modifier value", modifier);
            return;
        }

        int modifiers = field.getModifiers();
        fieldModifierField.setInt(field, modifier);
        logger.debug("For field {}, previous modifiers {}, new modifiers {}", field, modifiers, field.getModifiers());
    }

    public void setValue(Field field, @Nullable Object instance, @Nullable Object value) throws IllegalAccessException {
        Object v = field.get(instance);
        field.set(instance, value);
        logger.debug("For field {}, previous value {}, new value {}", field, v, value);
    }

}
