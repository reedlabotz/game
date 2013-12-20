# API

## POST /api/game/start

accepts

```
UserId: 12345
Players: [ playerObject ]
```

returns

```
{
  Success: true,
  Id: ‘abcd’
}
```

## GET /api/game/get

accepts

```
UserId: 12345
Id: ‘abcd’
```

returns

```
{
  Success: true,
  Game: gameObject,
  Move: moveObject
}
```

## POST api/game/move

accepts

```
UserId: 12345
GameId: ‘abcd’
Type: 0
Data: ‘The cat in the hat’
```

returns

```
{
  Success: true, 
  Id: ‘efgh’
}
```

## POST /api/game/end

accepts

```
UserId: 12345
Id: ‘abcd’
```

returns

```
{
  Success: true
}
```

## GET /api/game/review

accepts

```
UserId: 12345
Id: ‘abcd’
```

returns

```
{
  Success: true,
  Game: game,
  Moves: [ moveObject ]
}
```
