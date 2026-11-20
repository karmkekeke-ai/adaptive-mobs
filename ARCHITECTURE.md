# Architecture

## Persistent progression

`EvolutionData` is a `SavedData` object stored by the overworld. It advances once per second and exposes a clamped stage from 1 to 10. Nether and End ticks do not advance the clock, but mobs in those dimensions use the current world stage.

## Mob adaptation

`EvolutionEvents` applies the current stage to hostile mobs when they spawn and during periodic server ticks. NBT prevents repeating expensive setup. The stages progressively enable:

- movement and attack effects;
- loot pickup and target acquisition;
- zombie block breaking and late-game block placement;
- weapons, helmets and chest armor;
- fire resistance, regeneration and Aether infusion;
- slowing combat hits.

The current implementation uses vanilla mobs and items, leaving room for custom adaptive variants later.

## Artifacts

Artifacts are regular registered Forge items with server-authoritative right-click abilities. Their recipes are data-driven JSON files. At stage 5 or later, an unmarked chest has a chance to receive one artifact when a player opens it. Chest NBT prevents repeated rolls.

## Future extension points

- Add per-mob learning profiles instead of one global stage.
- Add custom AI goals for mining, crafting and coordinated raids.
- Add structures or loot-table injection for deliberate artifact dungeons.
- Add client particles and sounds for each artifact ability.
- Add a Forge config for stage duration, drop chance and difficulty caps.
