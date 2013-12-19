# API

## POST /api/game/start

accepts

```
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
