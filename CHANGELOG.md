# Changelog

All notable changes to InfinityChest are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

* * *

## [1.0.4]

### Fixed

- Fixed hoppers still stopping insertion once TileInfinityChest reaches 64 items — hopper insertion now works correctly for any count via IItemHandler bypass

* * *

## [1.0.3]

### Added

- Added an item tooltip matching StorageBox's style (contents, count/capacity, LC count, and a Shift-hold usage hint)
- Added the stored item's name to the chest's own display name, e.g. "Infinity Chest (Stone)"

### Fixed

- Fixed the GUI's icon and OUT slots visually capping at one below max-stack-size once the chest held 64+ items
- Fixed the tooltip usage hint omitting that right-click only opens the GUI with an empty hand, and missing the "right-click while holding an item inserts it" case

* * *

## [1.0.2]

### Fixed

- Fixed the LC count in the GUI being cut off when it reaches 3 digits (100 LC or more)
- Fixed hoppers temporarily stopping item transfer after inserting 64 items

* * *

## [1.0.1]

### Changed

- **Internal stability improvements**
    - Mod integration code has been reorganised to reduce the chance of conflicts with other mods.

* * *

## [1.0.0]

Initial release.
