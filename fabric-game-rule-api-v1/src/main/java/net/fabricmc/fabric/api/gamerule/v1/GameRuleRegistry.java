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

package net.fabricmc.fabric.api.gamerule.v1;

import net.fabricmc.fabric.impl.gamerule.RuleKeyExtensions;
import net.fabricmc.fabric.mixin.gamerule.GameRulesAccessor;
import net.minecraft.world.level.GameRules;

/**
 * A utility class which allows for registration of game rules.
 * Note game rules with duplicate keys are not allowed.
 * Checking if a game rule key is already taken can be done using {@link GameRuleRegistry#hasRegistration(String)}.
 *
 * <p>Creation of rule types is done using {@link GameRuleFactory}.
 *
 * @see GameRuleFactory
 */
public final class GameRuleRegistry {
	private GameRuleRegistry() {
	}

	/**
	 * Registers a {@link GameRules.Value}.
	 *
	 * @param name   the name of the rule
	 * @param category the category of this rule
	 * @param type the rule type
	 * @param <T>  the type of rule
	 * @return a rule key which can be used to query the value of the rule
	 * @throws IllegalStateException if a rule of the same name already exists
	 */
	public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
		return GameRules.register(name, category, type);
	}

	/**
	 * Registers a {@link GameRules.Value} with a custom category.
	 *
	 * @param name 	the name of the rule
	 * @param category the category of this rule
	 * @param type the rule type
	 * @param <T>  the type of rule
	 * @return a rule key which can be used to query the value of the rule
	 * @throws IllegalStateException if a rule of the same name already exists
	 */
	public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, CustomGameRuleCategory category, GameRules.Type<T> type) {
		final GameRules.Key<T> key = GameRules.register(name, GameRules.Category.MISC, type);
		((RuleKeyExtensions) (Object) key).fabric_setCustomCategory(category);
		return key;
	}

	/**
	 * Checks if a name for a game rule is already registered.
	 *
	 * @param ruleName the rule name to test
	 * @return true if the name is taken.
	 */
	public static boolean hasRegistration(String ruleName) {
		return GameRulesAccessor.getRuleTypes().keySet().stream().anyMatch(key -> key.getId().equals(ruleName));
	}
}
