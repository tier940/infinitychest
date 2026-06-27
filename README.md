# Infinity Chest

A Minecraft 1.12.2 Forge mod that adds a single-item bulk storage block.

## Features

- Transparent chest block
  - stores a single item type in configurable quantities (default capacity defined in `infinitychest.cfg`)
  - the stored item is visible inside the block, rotating in place
  - shows an enchantment glint when items are stored
  - tooltip shows the current capacity; if items are stored, also shows the stored item name and count
- Interactions (no GUI required)
  - right-click while holding an item: inserts the entire held stack
  - sneak + right-click: extracts one stack into your inventory
  - right-click empty-handed: opens the GUI
- GUI (ModularUI)
  - dedicated IN slot (insert-only) and OUT slot (take-only)
  - displays the stored item name, total item count, and LC count
- On block break
  - drops as a single chest item with its contents preserved (place it down to retrieve the stored items)
