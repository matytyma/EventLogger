# EventLogger
Minecraft plugin for inspecting data of events that occur

## Installation
Either download a prebuilt JAR from [releases](https://github.com/matytyma/EventLogger/releases/latest) or [build](#building) it from source

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

## Building
To build the project from source, first clone it with `git`
```shell
git clone https://github.com/matytyma/EventLogger.git
```
then `cd` into it
```shell
cd EventLogger
```
and finally build using gradle, for Linux and Mac
```shell
./gradlew build
```
and for Windows
```shell
./gradlew.bat build
```
Output JAR will be located in `build/libs` directory
