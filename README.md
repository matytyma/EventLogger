# EventLogger
Minecraft plugin for inspecting data of events that occur

## Configuration
`whitelist` - Whether to log just `events` or the rest instead

`events` - List of `Event` class names  be logged/excluded from logging

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
