package net.demilich.metastone.game.actions;

import co.paralleluniverse.fibers.Suspendable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.entities.Actor;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.targeting.EntityReference;
import net.demilich.metastone.game.targeting.TargetSelection;

/**
 * An action representing the playing of a minion card.
 * <p>
 * The {@link PlayCardAction#getTargetReference()} refers to the minion to whose left the minion should be summoned
 * (like inserting an element into an array). If {@code null}, the index passed to the summon function will be {@code
 * -1}, which indicates to summon the minion in the rightmost slot.
 * <p>
 * Typically, the battlecry is resolved in this action. The {@link #PlayMinionCardAction(net.demilich.metastone.game.targeting.EntityReference)}
 * will retrieve the battlecry that appears once the minion is summoned. Choose one minion cards override the battlecry
 * using {@link #PlayMinionCardAction(net.demilich.metastone.game.targeting.EntityReference, BattlecryAction)}.
 */
public class PlayMinionCardAction extends PlayCardAction implements HasBattlecry {

	private BattlecryAction overrideBattlecry;

	private PlayMinionCardAction() {
		super(null);
		setTargetRequirement(TargetSelection.FRIENDLY_MINIONS);
		setActionType(ActionType.SUMMON);
	}

	public PlayMinionCardAction(EntityReference EntityReference) {
		this(EntityReference, null);
	}

	public PlayMinionCardAction(EntityReference EntityReference, BattlecryAction overrideBattlecry) {
		super(EntityReference);
		this.overrideBattlecry = overrideBattlecry;
		setTargetRequirement(TargetSelection.FRIENDLY_MINIONS);
		setActionType(ActionType.SUMMON);
	}

	@Override
	@Suspendable
	protected void play(GameContext context, int playerId) {
		Card card = context.getPendingCard();
		Actor nextTo = (Actor) (getTargetReference() != null ? context.resolveSingleTarget(getTargetReference()) : null);
		Minion minion = card.summon();
		if (overrideBattlecry != null) {
			minion.setBattlecry(overrideBattlecry);
		}
		Player player = context.getPlayer(playerId);
		int index = player.getMinions().indexOf(nextTo);
		context.getLogic().summon(playerId, minion, card, index, true);
	}

	@Override
	public BattlecryAction getBattlecryAction() {
		return overrideBattlecry;
	}

	@Override
	public void setBattlecryAction(BattlecryAction action) {
		overrideBattlecry = action;
	}
}
