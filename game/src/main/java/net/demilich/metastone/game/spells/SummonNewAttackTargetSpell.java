package net.demilich.metastone.game.spells;

import co.paralleluniverse.fibers.Suspendable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

@Deprecated
public class SummonNewAttackTargetSpell extends Spell {

	@Override
	@Suspendable
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		SummonSpell summonSpell = new SummonSpell();
		SpellDesc overrideTargetSpell = OverrideTargetSpell.create();
		overrideTargetSpell.setTarget(EntityReference.OUTPUT);
		desc.put(SpellArg.SPELL, overrideTargetSpell);
		summonSpell.onCast(context, player, desc, source, target);
	}
}
