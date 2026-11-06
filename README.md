# Player Animator for Minecraft 1.12.2

A 1.12.2 / Forge port of [KosmX's Player Animator](https://github.com/KosmX/minecraftPlayerAnimator) (upstream branch `1.20`, commit `a17ecb8`, `1.0.2-rc1`).

The library evaluates player animations **client-side only**. Every client runs the same
animation layers for all visible players, so other players see your animation without
any packet being sent. There is no network layer.

## Requirements

| Item | Version |
|---|---|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2859 |
| Java | 8 (bytecode target) |
| MixinBooter | 10.7+ (tested with 11.17) |

`MixinBooter` is required: the mod uses a small FML coremod to register its mixins.

## Build

Gradle 8.1.1 wrapper is included. Gradle itself needs a newer JDK, while the project
compiles with Java 8:

```bash
export JAVA_HOME="<path to JDK 21>"
./gradlew build
```

The output jar is `build/libs/playeranimator-1.12.2-<version>.jar`.

## Install

Drop the jar into `mods/` together with MixinBooter. The mod is client-side only
(`clientSideOnly = true`) and can be installed on a server without effect.

## API

```java
// per-player animation stack (client side)
AnimationStack stack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
stack.addAnimLayer(1000, new ModifierLayer<>());

// register a layer for every client player
PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
        new ResourceLocation("mymod", "my_layer"), 1000, player -> new ModifierLayer<>());

// play a clip
ModifierLayer<IAnimation> layer = (ModifierLayer<IAnimation>)
        PlayerAnimationAccess.getPlayerAssociatedData(player).get(id);
layer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(8, Ease.INOUTSINE),
        new KeyframeAnimationPlayer(clip));

// registry helpers
PlayerAnimationRegistry.getAnimation(id);
PlayerAnimationRegistry.serializeTextToString("\"hold_upper\"");
```

Clips use the upstream JSON format (Emotecraft or GeckoLib `.player_animation.json`).

## Pose mapping

The animation is applied to the vanilla `ModelBiped` parts after
`setRotationAngles`:

| part | 1.12.2 part |
|---|---|
| `head` | `bipedHead` (+ `bipedHeadwear`) |
| `torso` | `bipedBody` |
| `rightArm` / `leftArm` | `bipedRightArm` / `bipedLeftArm` |
| `rightLeg` / `leftLeg` | `bipedRightLeg` / `bipedLeftLeg` |
| `body` | model root matrix (`RenderPlayer#applyRotations`) |

## Known limitations

- **Bend is not supported.** 1.12.2 has no `ModelPart` / custom vertex consumer support;
  the upstream bendy-lib code is not ported.
- **No first-person animation.** Matches upstream `FirstPersonMode.NONE` semantics.
- **No automatic left-hand mirroring.** `minecraftApi.layers.LeftHandedHelperModifier`
  is provided for callers to add, as upstream does.
- Debug: `-Dplayeranimator.debugModel=true` logs biped rotations once per second.

## Notes

- This repository contains the library only. The TaCZ-Legacy compatibility layer
  (`com.tacz.legacy.client.compat.playeranimator`) lives in TaCZ-Legacy and is GPLv3.
- Original project: https://github.com/KosmX/minecraftPlayerAnimator (MIT).

## License

MIT. See [LICENSE](LICENSE).
