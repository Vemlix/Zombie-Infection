package com.zalthonethree.zombieinfection.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zalthonethree.zombieinfection.ZombieInfection;
import com.zalthonethree.zombieinfection.api.ZombieInfectionAPI;
import com.zalthonethree.zombieinfection.potion.PotionHelper;
import com.zalthonethree.zombieinfection.utility.LogHelper;

public class InfectionEvent /*extends EntityDragon*/ {
	@SubscribeEvent public void onAttack(LivingHurtEvent event) {
		if (event.source instanceof EntityDamageSource) {
			EntityDamageSource source = (EntityDamageSource) event.source;
			Entity attacker = source.getEntity();
			boolean infectiousMob = false;
			if (attacker instanceof EntityZombie) infectiousMob = true;
			for (int entityId : ZombieInfectionAPI.getCustionInfectiousMobs()) {
				LogHelper.warn(EntityList.getIDFromString(EntityList.getEntityString(attacker)));
				if (EntityList.getEntityID(attacker) == entityId) {
					infectiousMob = true;
				}
			}
			if (infectiousMob) {
				Entity target = event.entity;
				if (target instanceof EntityPlayer) {
					EntityPlayer attacked = (EntityPlayer) target;
					if ((attacked.getRNG().nextInt(100) + 1) <= 10) {
						attacked.addChatMessage(new ChatComponentTranslation("zombieinfection.chat.infected"));
						attacked.addPotionEffect(PotionHelper.createInfection(0));
					}
				}
			} else if (attacker instanceof EntityPlayer) {
				Entity target = event.entity;
				if (target instanceof EntityPlayer) {
					EntityPlayer attacked = (EntityPlayer) target;
					EntityPlayer possiblespreader = (EntityPlayer) attacker;
					if (possiblespreader.isPotionActive(ZombieInfection.potionInfection)) {
						attacked.addPotionEffect(PotionHelper.createInfection(0));
					}
				}
			}
		}
	}
}