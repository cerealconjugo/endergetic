package com.minecraftabnormals.endergetic.common.network.entity;

import com.minecraftabnormals.endergetic.client.events.OverlayEvents;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class S2CEnablePurpoidFlash {

	public void serialize(PacketBuffer buf) {}

	public static S2CEnablePurpoidFlash deserialize(PacketBuffer buf) {
		return new S2CEnablePurpoidFlash();
	}

	public static void handle(S2CEnablePurpoidFlash message, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			context.enqueueWork(OverlayEvents::enablePurpoidFlash);
		}
		context.setPacketHandled(true);
	}

}