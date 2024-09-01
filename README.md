<img align="right" src="logo.svg" alt="TOML logo">

# EventLogger
Minecraft plugin for inspecting data of events that occur

> [!NOTE]  
> This project is quickly evolving and I can't keep the README up-to-date with every update,
> if you'd like to build/use this project, contact me using any of the following methods  
> [Discord](https://discord.com/users/803549121247838209) - `matytyma`  
> [HackClub Slack](https://hackclub.slack.com/team/U078H6SG59Q) - `@matytyma`

## Installation
Either download a prebuilt JAR from [releases](https://github.com/matytyma/EventLogger/releases/latest)
or [build](#building) it from source

## Configuration
`whitelist` - Whether to log just `events` or the rest instead

`events` - List of `Event` class names  be logged/excluded from logging

`format` - Formatting options to style the generated output
<details>
    <summary>Visual representation of formatting options</summary>

    Block(position=[109, 97, 5871], type=Material.BEDROCK, data=BlockData(data=minecraft:bedrock))
         ^         ^       ^^    ^      ^                ^^                                      ^
         |         |       |     |      |                |                                       |
         |         |       |     |      |                - class.separator         class.postfix -
         |         |       |     |      - field.separator
         |         |       |     - array.postfix
         |         |       - array.separator
         |         - array.prefix
         - class.prefix
</details>

### Examples
Log all block-related events
```toml
whitelist = [ 'BlockEvent' ]
```
Log all inventory-related events, block-related events and player movement event
```toml
whitelist =  [
    'InventoryEvent',
    'BlockEvent',
    'PlayerMoveEvent',
]
```
Log all events except player-related ones and inventory open event
```toml
whitelist = [ '*' ]

blacklist = [
    'PlayerEvent',
    'InventoryOpenEvent',
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
