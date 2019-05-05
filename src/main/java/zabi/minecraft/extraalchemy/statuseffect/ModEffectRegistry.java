package zabi.minecraft.extraalchemy.statuseffect;

import java.lang.reflect.Field;

import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import zabi.minecraft.extraalchemy.config.ConfigInstance;
import zabi.minecraft.extraalchemy.config.ModConfig;
import zabi.minecraft.extraalchemy.statuseffect.effects.MagnetismStatusEffect;
import zabi.minecraft.extraalchemy.statuseffect.effects.PhotosynthesisStatusEffect;
import zabi.minecraft.extraalchemy.utils.LibMod;
import zabi.minecraft.extraalchemy.utils.Log;

public class ModEffectRegistry {

	public static ModStatusEffect magnetism = new MagnetismStatusEffect(StatusEffectType.BENEFICIAL, 0xb8b8b8, false);
	public static ModStatusEffect photosynthesis = new PhotosynthesisStatusEffect(StatusEffectType.BENEFICIAL, 0x3cbd19, false);

	public static void init() {

		try {
			int registered = 0;
			int disabled = 0;
			for (Field field:ModEffectRegistry.class.getDeclaredFields()) {
				if (ModStatusEffect.class.isAssignableFrom(field.getType())) {
					boolean active = (boolean) ConfigInstance.Potions.class.getField(field.getName()).get(ModConfig.INSTANCE.potions);
					Identifier id = new Identifier(LibMod.MOD_ID, field.getName());
					if (active) {
						Registry.register(Registry.STATUS_EFFECT, id, ((ModStatusEffect) field.get(null)).onRegister());
						Log.d("Registered potion "+id);
						registered++;
					} else {
						Log.d("Potion disabled: "+id);
						disabled++;
					}
				}
			}
			int total = registered + disabled;
			Log.i("Registered %d/%d potions, %d were disabled", registered, total, disabled);
		} catch (Exception e) {
			Log.printAndPropagate(e);
		}

	}

}