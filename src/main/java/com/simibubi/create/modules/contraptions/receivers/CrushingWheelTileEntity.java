package com.simibubi.create.modules.contraptions.receivers;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.modules.contraptions.base.KineticTileEntity;

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;

public class CrushingWheelTileEntity extends KineticTileEntity {

	public CrushingWheelTileEntity() {
		super(AllTileEntities.CRUSHING_WHEEL.type);
	}

	@Override
	public void onSpeedChanged() {
		super.onSpeedChanged();
		for (Direction d : Direction.values())
			((CrushingWheelBlock) getBlockState().getBlock()).updateControllers(getBlockState(), getWorld(), getPos(),
					d);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos).grow(1);
	}

}
