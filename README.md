# EventLogger
Minecraft plugin for inspecting data of events that occur

## Configuration
Add a list of events to be logged should be added to `events` property in `config.yml` file.

### Examples
Log all block-related events
```yaml
whitelist: true

events: [
  BlockEvent
]
```
Log all inventory-related events, block-related events and player movement event
```yaml
whitelist: true

events: [
  InventoryEvent,
  BlockEvent,
  PlayerMoveEvent
]
```
Log all events except player-related ones and inventory open event
```yaml
whitelist: false

events: [
  PlayerEvent,
  InventoryOpenEvent
]
```
