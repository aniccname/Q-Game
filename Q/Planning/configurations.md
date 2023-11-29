# Changes to support configurations

- Added `Config` package with configuration implementations
  - Added a builder for each configuration
- Changed `ActionScorer` to take a `ScoringConfig` instead of two integers in the constructor
- Changed `GameState` to take a `ScoringConfig` in the constructor instead of having to manually edit two constants
  - `GameState` now provides the `ScoringConfig` to the `ActionScorer`
- Changed the `IReferee` interface and the `Referee` implementation to take a `RefereeConfig` as an argument to the `playGame` method
- Changed `Server` to take a `ServerConfig` in the constructor instead of having to manually edit timeout constants
  - `Server` now provides the `RefereeConfig` to the `Referee`
  - The player timeout provided to `PlayerSafetyAdapter` now comes from the `ServerConfig` instead of a constant in `Server`
