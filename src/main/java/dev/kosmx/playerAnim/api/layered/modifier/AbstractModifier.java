package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.layered.AnimationContainer;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
public abstract class AbstractModifier extends AnimationContainer<IAnimation> {

    /**
     * ModifierLayer, if you want to do something really fancy!
     * Shouldn't be null when playing
     */
    @Nullable
    protected ModifierLayer host;

    public void setHost(@Nullable ModifierLayer host) {
        this.host = host;
    }

    public AbstractModifier() {
        super(null);
    }

    /**
     * @return modifier can be removed.
     */
    public boolean canRemove() {
        return false;
    }


}
