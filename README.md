# RGB Express

A solver for the game [RGB Express](http://rgbexpress.com).

Not all levels are available since typing them taks some time, if you want to contribute some levels you're welcome.

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

## Elements to be used in maps:

```
empty ' '

blue truck ʙ
blue package b
blue warehouse B

green truck ɢ
green package g
green warehouse G

red truck ʀ
red package r
red warehouse R

white truck ᴡ

yellow truck ʏ
yellow package y
yellow warehouse Y

switch 1 button enabled ᵘ
switch 1 button disabled ᵤ
switch 1 road open u
switch 1 road closed U

switch 2 button enabled ᵛ
switch 2 button disabled ᵥ
switch 2 road open v
switch 2 road closed V

switch 3 button enabled ᵒ
switch 3 button disabled ₒ
switch 3 road open o
switch 3 road closed O
    
switch 4 button enabled ⁱ
switch 4 button disabled ᵢ
switch 4 road open i
switch 4 road closed I

bump ○

crossing ┼
left right ─
up down │
left up ┘
right up └
left down ┐
right down ┌
left ╴
right ╶
up ╵
down ╷
left up down ┤
right up down ├
left right up ┴
left right down ┬
```

Compact version:
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
○
    
    ╷  
 ┌─┬┼┐ 
 │ │││ 
 ├─┼┼┤ 
╶┼─┼┼┼╴
 └─┴┼┘ 
    ╵ 
```
 
## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/archiloque/rgbexpress .
This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

The code is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).

## Code of Conduct

Everyone interacting in the project’s codebases, issue trackers, chat rooms and mailing lists is expected to follow the [code of conduct](https://github.com/archiloque/rgbexpress/blob/master/CODE_OF_CONDUCT.md).
