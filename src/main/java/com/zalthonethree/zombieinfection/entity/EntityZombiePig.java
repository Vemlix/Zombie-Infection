package com.zalthonethree.zombieinfection.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import com.zalthonethree.zombieinfection.api.IZombieInfectionMob;

public class EntityZombiePig extends EntityMob implements IZombieInfectionMob{
	public EntityZombiePig(World world) {
		super(world);
		this.setSize(0.9F, 0.9F);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPig>(this, EntityPig.class, false));
	}
	
	@Override protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}
	
	@Override public boolean attackEntityAsMob(Entity entity) {
		boolean flag = super.attackEntityAsMob(entity);
		
		if (flag) {
			int i = this.worldObj.getDifficulty().getDifficultyId();
			
			if (this.getHeldItemMainhand() == null && this.isBurning() && this.rand.nextFloat() < (float) i * 0.3F) {
				entity.setFire(2 * i);
			}
		}
		
		return flag;
	}
	
	@Override protected SoundEvent getAmbientSound() { return SoundEvents.entity_pig_ambient; }
	@Override protected SoundEvent getHurtSound() { return SoundEvents.entity_pig_hurt; }
	@Override protected SoundEvent getDeathSound() { return SoundEvents.entity_pig_death; }
	
	@Override protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.entity_pig_step, 0.15F, 1.0F);
	}
	
	@Override protected float getSoundVolume() { return 0.4F; }
	@Override public EnumCreatureAttribute getCreatureAttribute() { return EnumCreatureAttribute.UNDEAD; }
	@Override protected Item getDropItem() { return Items.rotten_flesh; }
	
	@Override public void onLivingUpdate() {
		if (this.worldObj.isDaytime() && !this.worldObj.isRemote && !this.isChild()) {
			float f = this.getBrightness(1.0F);
			
			if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)))) {
				this.setFire(8);
			}
		}
		
		super.onLivingUpdate();
	}
	
	@Override public void onKillEntity(EntityLivingBase entityLivingIn) {
		super.onKillEntity(entityLivingIn);
		
		if ((this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD && !entityLivingIn.isChild()) && entityLivingIn instanceof EntityPig) {
			if (this.worldObj.getDifficulty() != EnumDifficulty.HARD && this.rand.nextBoolean()) { return; }
			
			EntityZombiePig entityzombiepig = new EntityZombiePig(this.worldObj);
			entityzombiepig.copyLocationAndAnglesFrom(entityLivingIn);
			this.worldObj.removeEntity(entityLivingIn);
			entityzombiepig.onInitialSpawn(this.worldObj.getDifficultyForLocation(this.getPosition()), (IEntityLivingData) null);
			
			this.worldObj.spawnEntityInWorld(entityzombiepig);
			this.worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1016, new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ), 0);
		}
	}
	
	@Override public int getPlayerInfectionChance() { return 10; }
}