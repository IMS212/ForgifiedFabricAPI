/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.screen.v1;

import java.util.List;
import java.util.Objects;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

/**
 * Utility methods related to screens.
 *
 * @see ScreenEvents
 */
public final class Screens {
	/**
	 * Gets all of a screen's button widgets.
	 * The provided list allows for addition and removal of buttons from the screen.
	 * This method should be preferred over adding buttons directly to a screen's {@link Screen#children() child elements}.
	 *
	 * @return a list of all of a screen's buttons
	 */
	public static List<AbstractWidget> getButtons(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getButtons();
	}

	/**
	 * Gets a screen's text renderer.
	 *
	 * @return the screen's text renderer.
	 */
	public static Font getTextRenderer(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ((ScreenAccessor) screen).getFont();
	}

	public static Minecraft getClient(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ((ScreenAccessor) screen).getMinecraft();
	}

	private Screens() {
	}
}
