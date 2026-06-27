# Infinity Chest

A Minecraft 1.12.2 Forge mod that adds a single-item bulk storage block.

## Features

- Transparent chest block
  - stores a single item type in configurable quantities (default capacity defined in `InfinityChestConfigHolder`)
  - the stored item is visible inside the block, rotating in place
  - tooltip shows the current capacity
- Interactions (no GUI required)
  - right-click while holding an item: inserts the entire held stack
  - sneak + right-click: extracts one stack into your inventory
  - right-click empty-handed: opens the GUI
- GUI (ModularUI)
  - dedicated IN slot (insert-only) and OUT slot (take-only)
  - displays the stored item name, total item count, and LC count
- On block break
  - all stored items are dropped as stacks
