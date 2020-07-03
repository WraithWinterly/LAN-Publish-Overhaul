package com.IsRare.PublishOverhaul.mixins;

import net.minecraft.client.util.NetworkUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(NetworkUtils.class)
public class LANPortMixin {
	@Overwrite
	public static int findLocalPort() {
		return 25565;
	}
}
