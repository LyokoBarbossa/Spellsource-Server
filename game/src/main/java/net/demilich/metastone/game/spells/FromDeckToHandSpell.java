package net.demilich.metastone.game.spells;

import co.paralleluniverse.fibers.Suspendable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardList;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.EntityType;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;

public class FromDeckToHandSpell extends Spell {

	@Override
	@Suspendable
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		if (target != null && target.getEntityType() == EntityType.CARD) {
			Card card = (Card) target;
			if (player.getDeck().containsCard(card)) {
				context.getLogic().receiveCard(player.getId(), card, source, true);
			}
			return;
		}

		int value = desc.getValue(SpellArg.VALUE, context, player, target, source, 1);
		String replacementCard = (String) desc.get(SpellArg.CARD);

		EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);
		CardList relevantCards = null;
		if (cardFilter != null) {
			relevantCards = SpellUtils.getCards(player.getDeck(), card -> cardFilter.matches(context, player, card, source));
		} else {
			relevantCards = SpellUtils.getCards(player.getDeck(), null);
		}
		for (int i = 0; i < value; i++) {
			Card card = null;
			if (!relevantCards.isEmpty()) {
				card = context.getLogic().getRandom(relevantCards);
				relevantCards.remove(card);
			} else if (replacementCard != null) {
				card = context.getCardById(replacementCard);
			}

			if (card != null) {
				context.getLogic().receiveCard(player.getId(), card, source, true);
			}
		}
	}

}
