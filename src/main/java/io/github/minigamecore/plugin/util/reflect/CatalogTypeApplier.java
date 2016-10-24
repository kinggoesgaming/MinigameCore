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

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github.minigamecore.api.util.manager.GuiceManager;
import io.github.minigamecore.plugin.MinigameCore;
import io.github.minigamecore.plugin.util.logger.MinigameCoreLogger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

/**
 * Applies {@code assets/minigamecore/mcap/catalog.json.gz} or
 * {@code assets/minigamecore/mcap/catalog.json}.
 */
public final class CatalogTypeApplier {

    private final Logger logger = new MinigameCoreLogger("catalog");
    private final MinigameCore plugin;
    private final Injector injector;
    private final Reflection reflection;
    private ConfigurationNode node;

    @Inject
    private CatalogTypeApplier(Reflection reflection, MinigameCore plugin, GuiceManager guiceManager) {
        this.reflection = reflection;
        this.plugin = plugin;
        this.injector = guiceManager.getInjector();
    }

    public void apply() {

        try {

            if (discoverCatalogFile()) {
                int v = checkVersion();

                switch (v) {
                    case 0:
                        logger.warn("Could not apply catalog mappings. Is the `version` present and greater than 0");
                        break;
                    case 1:
                        logger.debug("Version 1 catalog mappings found, applying");
                        applyV1Mappings();
                        break;
                    default:
                        logger.warn("{} is not a valid version number", v);
                }
            }
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    private ConfigurationNode getNode() {
        return node;
    }

    private boolean discoverCatalogFile() throws IOException {
        Optional<Asset> assetOptional = Sponge.getAssetManager().getAsset(plugin, "mcap/catalog.json.gz");

        if (!assetOptional.isPresent()) {
            assetOptional = Sponge.getAssetManager().getAsset(plugin, "mcap/catalog.json");

            if (!assetOptional.isPresent()) {
                logger.warn("No catalog mappings file found. This will be a problem");
                return false;
            }

            node = GsonConfigurationLoader.builder().setURL(assetOptional.get().getUrl()).build().load();
            return true;
        }

        Optional<Asset> finalAssetOptional = assetOptional;
        //noinspection OptionalGetWithoutIsPresent
        node = GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(new InputStreamReader(new GZIPInputStream(finalAssetOptional.get()
                .getUrl().openStream())))).build().load();
        return true;
    }

    private int checkVersion() {
        return node.getNode("version").getInt();
    }

    private void applyV1Mappings() {
        getNode().getNode("mappings").getChildrenMap().forEach((typeKey, value) -> {
            final String type = (String) typeKey;

            value.getChildrenMap().forEach((containerKey, val) -> {
                try {
                    Class clazz = Class.forName((String) containerKey);

                    val.getChildrenList().forEach(v -> {
                        try {
                            Field field = clazz.getField((String) v.getKey());
                            if (typeCheck(type, field)) {
                                applyReflections(field, injector.getInstance(Class.forName((String) v.getValue())));
                                return;
                            }

                            logger.warn("Cannot apply value to field {} in class {}, as the types don't match", field, clazz.getCanonicalName());
                        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                            logger.debug(e.getMessage(), e);
                        }
                    });
                } catch (ClassNotFoundException e) {
                    logger.debug(e.getMessage(), e);
                }
            });
        });
    }

    private void applyReflections(Field field, Object value) throws NoSuchFieldException, IllegalAccessException {
        reflection.setAccessible(field, true);
        reflection.setModifier(field, Modifier.FINAL);
        reflection.setValue(field, null, value);
    }

    private boolean typeCheck(String classType, Field field) {
        if (field.getClass().getTypeName().equals(classType)) {
            return true;
        }

        if (field.getClass().getSuperclass().getTypeName().equals(classType)) {
            return true;
        }

        for (Class clazz : field.getClass().getInterfaces()) {
            if (clazz.getTypeName().equals(classType)) {
                return true;
            }
        }

        return false;
    }

}
