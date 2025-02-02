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

package net.fabricmc.fabric.impl.recipe.ingredient.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class AnyIngredient extends CombinedIngredient {
	private static final MapCodec<AnyIngredient> ALLOW_EMPTY_CODEC = createCodec(Ingredient.CODEC);
	private static final MapCodec<AnyIngredient> DISALLOW_EMPTY_CODEC = createCodec(Ingredient.CODEC_NONEMPTY);

	private static MapCodec<AnyIngredient> createCodec(Codec<Ingredient> ingredientCodec) {
		return ingredientCodec
				.listOf()
				.fieldOf("ingredients")
				.xmap(AnyIngredient::new, AnyIngredient::getIngredients);
	}

	public static final CustomIngredientSerializer<AnyIngredient> SERIALIZER =
			new CombinedIngredient.Serializer<>(ResourceLocation.fromNamespaceAndPath("fabric", "any"), AnyIngredient::new, ALLOW_EMPTY_CODEC, DISALLOW_EMPTY_CODEC);

	public AnyIngredient(List<Ingredient> ingredients) {
		super(ingredients);
	}

	@Override
	public boolean test(ItemStack stack) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> previewStacks = new ArrayList<>();

		for (Ingredient ingredient : ingredients) {
			previewStacks.addAll(Arrays.asList(ingredient.getItems()));
		}

		return previewStacks;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
