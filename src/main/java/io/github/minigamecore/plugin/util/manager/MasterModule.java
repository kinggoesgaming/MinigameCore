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

package io.github.minigamecore.plugin.util.manager;

import com.google.inject.AbstractModule;
import io.github.minigamecore.api.MinigameService;
import io.github.minigamecore.api.util.config.ConfigurationManager;
import io.github.minigamecore.api.util.manager.GuiceManager;
import io.github.minigamecore.plugin.config.ConfigurationManagerImpl;
import io.github.minigamecore.plugin.config.ConfigurationModule;
import io.github.minigamecore.plugin.service.MinigameServiceImpl;
import io.github.minigamecore.plugin.util.logger.MinigameCoreLoggerModule;

/**
 * The main guice module.
 *
 * <p>
 *     The only module that is not included here is the
 *     {@link MinigameCoreLoggerModule} as it needs to be active before this
 *     module is called.
 * </p>
 */
public final class MasterModule extends AbstractModule {

    protected void configure() {
        bind(ConfigurationManager.class).to(ConfigurationManagerImpl.class);
        bind(GuiceManager.class).to(GuiceManagerImpl.class);

        install(new ConfigurationModule());

        bind(MinigameService.class).to(MinigameServiceImpl.class);
    }

}
