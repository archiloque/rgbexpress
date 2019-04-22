# RGB Express

A WIP solver for the game RGB Express.

## Usage

Build:

```
mvn install
```

Run all levels:
```
java -jar target/rgbexpress-1.0-SNAPSHOT.jar 
```

Run a specific level:
```
java -jar target/rgbexpress-1.0-SNAPSHOT.jar levels/A/04
```

## Elements to be used in maps

```
EMPTY ' '

BLUE_TRUCK ʙ
BLUE_PACKAGE b
BLUE_WAREHOUSE B

GREEN_TRUCK ɢ
GREEN_PACKAGE g
GREEN_WAREHOUSE G

RED_TRUCK ʀ
RED_PACKAGE r
RED_WAREHOUSE R

WHITE_TRUCK ᴡ

YELLOW_TRUCK ʏ
YELLOW_PACKAGE y
YELLOW_WAREHOUSE Y

SWITCH_1_BUTTON_ENABLED ᵘ
SWITCH_1_BUTTON_DISABLED ᵤ
SWITCH_1_ROAD_OPEN u
SWITCH_1_ROAD_CLOSED U

SWITCH_2_BUTTON_ENABLED ᵛ
SWITCH_2_BUTTON_DISABLED ᵥ
SWITCH_2_ROAD_OPEN v
SWITCH_2_ROAD_CLOSED V

SWITCH_3_BUTTON_ENABLED ᵒ
SWITCH_3_BUTTON_DISABLED ₒ
SWITCH_3_ROAD_OPEN o
SWITCH_3_ROAD_CLOSED O
    
CROSSING ┼
LEFT_RIGHT ─
UP_DOWN │
LEFT_UP ┘
RIGHT_UP └
LEFT_DOWN ┐
RIGHT_DOWN ┌
LEFT ╴
RIGHT ╶
UP ╵
DOWN ╷
LEFT_UP_DOWN ┤
RIGHT_UP_DOWN ├
LEFT_RIGHT_UP ┴
LEFT_RIGHT_DOWN ┬
```

Short version :
```
ʙbB
ɢgG
ʀrR
ᴡ
ʏyY
ᵘᵤU
ᵛᵥvV
ᵒₒoO
    
┼

─│

┘└┐┌

╴╶╵╷

┤├┴┬
```
 
## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/archiloque/A WIP solver for the game RGB Express.
This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

The code is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Code of Conduct

Everyone interacting in the project’s codebases, issue trackers, chat rooms and mailing lists is expected to follow the [code of conduct](https://github.com/archiloque/rgbexpress/blob/master/CODE_OF_CONDUCT.md).
