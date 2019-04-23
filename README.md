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

BLUE TRUCK ʙ
BLUE PACKAGE b
BLUE WAREHOUSE B

GREEN TRUCK ɢ
GREEN PACKAGE g
GREEN WAREHOUSE G

RED TRUCK ʀ
RED PACKAGE r
RED WAREHOUSE R

WHITE TRUCK ᴡ

YELLOW TRUCK ʏ
YELLOW PACKAGE y
YELLOW WAREHOUSE Y

SWITCH 1 BUTTON ENABLED ᵘ
SWITCH 1 BUTTON DISABLED ᵤ
SWITCH 1 ROAD OPEN u
SWITCH 1 ROAD CLOSED U

SWITCH 2 BUTTON ENABLED ᵛ
SWITCH 2 BUTTON DISABLED ᵥ
SWITCH 2 ROAD OPEN v
SWITCH 2 ROAD CLOSED V

SWITCH 3 BUTTON ENABLED ᵒ
SWITCH 3 BUTTON DISABLED ₒ
SWITCH 3 ROAD OPEN o
SWITCH 3 ROAD CLOSED O
    
SWITCH 4 BUTTON ENABLED ⁱ
SWITCH 4 BUTTON DISABLED ᵢ
SWITCH 4 ROAD OPEN i
SWITCH 4 ROAD CLOSED I

CROSSING ┼
LEFT RIGHT ─
UP DOWN │
LEFT UP ┘
RIGHT UP └
LEFT DOWN ┐
RIGHT DOWN ┌
LEFT ╴
RIGHT ╶
UP ╵
DOWN ╷
LEFT UP DOWN ┤
RIGHT UP DOWN ├
LEFT RIGHT UP ┴
LEFT RIGHT DOWN ┬
```

Short version :
```
ʙbB
ɢgG
ʀrR
ᴡ
ʏyY
ᵘᵤuU
ᵛᵥvV
ᵒₒoO
ⁱᵢiI
    
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
