package me.corriekay.pokegoutil.data.models.operations;

import com.pokegoapi.exceptions.InvalidCurrencyException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import me.corriekay.pokegoutil.data.enums.OperationError;
import me.corriekay.pokegoutil.data.managers.AccountManager;
import me.corriekay.pokegoutil.data.models.BpmOperationResult;
import me.corriekay.pokegoutil.data.models.PokemonModel;
import me.corriekay.pokegoutil.gui.enums.OperationId;
import me.corriekay.pokegoutil.utils.ConfigKey;

public class PowerupOperation extends Operation {

    /**
     * Instantiate PowerupOperation. Only used in mocking.
     */
    protected PowerupOperation() {
        // For mocking
        super();
    }

    /**
     * Instantiate PowerupOperation with a pokemon.
     *
     * @param pokemon pokemon to power up
     */
    public PowerupOperation(final PokemonModel pokemon) {
        super(pokemon);
    }

    @Override
    protected BpmOperationResult doOperation() {
        return new BpmOperationResult("Not implemented", OperationError.NOT_IMPLEMENTED);
    }

    @Override
    protected int getMaxDelay() {
        return config.getInt(ConfigKey.DELAY_POWERUP_MAX);
    }

    @Override
    protected int getMinDelay() {
        return config.getInt(ConfigKey.DELAY_POWERUP_MIN);
    }

    @Override
    public OperationId getOperationId() {
        return OperationId.POWERUP;
    }

    @Override
    public BpmOperationResult validateOperation()
            throws InvalidCurrencyException, LoginFailedException, RemoteServerException {
        if (pokemon.isInGym()) {
            return new BpmOperationResult("Pokemon is in gym", OperationError.IN_GYM);
        }

        final int candies = pokemon.getCandies();
        final int candiesToPowerup = pokemon.getCandyCostsForPowerup();
        if (!enoughSource(candies, candiesToPowerup)) {
            return needSomeToPowerUp(candies, candiesToPowerup,OperationError.INSUFFICENT_CANDIES);
        }

        final int stardust = AccountManager.getInstance().getPlayerAccount().getStardust();
        final int stardustToPowerUp = pokemon.getStardustCostsForPowerup();

        if (!enoughSource(stardust, stardustToPowerUp)) {
            return needSomeToPowerUp(candies, candiesToPowerup,OperationError.INSUFFICENT_STARDUSTS);
        }

        return new BpmOperationResult();
    }

    private boolean enoughSource(final int exist, final int needToPowerUp) {
        return exist >= needToPowerUp;
    }

    private BpmOperationResult needSomeToPowerUp(final int exist, final int needToPowerUp, OperationError operationError) {
        return new BpmOperationResult(String.format(
                "Insufficent candies, needed %d but had %d ",
                exist,
                needToPowerUp),
                operationError);
    }
}
