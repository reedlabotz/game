package scribblevine

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"time"
	
	"appengine"
	"appengine/datastore"
)

type Move struct {
	UserId string `json:"userId"`
	Type int `json:"type"`
	Data []byte `json:"data"`
	Timestamp time.Time `json:"timestamp"`
}

type Game struct {
	Id string `json:"id",datastore:"-"`
	UserId string `json:"userId"`
	Started time.Time `json:"started"`
  Players []string `json:"players"`
}

type QueuePlayer struct {
	UserId string `json:"userId"`
	Timestamp time.Time `json:"timestamp"`
}

func init() {
	http.HandleFunc("/api/alive", alive)
	http.HandleFunc("/api/queue/get", queueGet)
	http.HandleFunc("/api/game/start", gameStart)
	http.HandleFunc("/api/game/get", gameGet)
	http.HandleFunc("/api/game/move", gameMove)
}

func alive(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "yes")
}

func createNewGame(c appengine.Context, userId string, players []string) (Game, error) {
	g := Game{
		UserId: userId,
		Started: time.Now(),
		Players: players,
	}
	err := datastore.RunInTransaction(c, func(c appengine.Context) error {
		key, err := datastore.Put(c, datastore.NewIncompleteKey(c, "game", nil), &g)

		if err != nil {
			return err
		}

		// For now just add all players into the queue.
		for _,p := range players {
			qp := QueuePlayer {
				UserId: p,
				Timestamp: time.Now(),
			}
			_, err := datastore.Put(c, datastore.NewIncompleteKey(c, "queueplayer", key), &qp)

			if err != nil {
				return err
			}
		}
		
		g.Id = key.Encode()

		return nil
	}, nil)
	
	return g, err
}

func gameStart(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	userId := r.FormValue("userId")
	players := append(strings.Split(r.FormValue("players"),","), userId)

	g, err := createNewGame(c, userId, players)

	data, err := json.Marshal(g)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)	
	}

	w.Write(data)
}

type GameGetResponse struct {
	Type int
	Data string
}

func gameGet(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	id := r.FormValue("Id")
	gameKey, err := datastore.DecodeKey(id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	q := datastore.NewQuery("move").Ancestor(gameKey).Order("-Timestamp").Limit(1)
	moves := make([]Move, 0, 1)
	if _, err := q.GetAll(c, &moves); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	
	response := GameGetResponse{
		Type: moves[0].Type,
		Data: string(moves[0].Data),
	}

	data, err := json.Marshal(response)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Write(data)
}

type GameMoveResponse struct {
	Success bool
	Id string
}

func gameMove(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	id := r.FormValue("GameId")

	gameKey, err := datastore.DecodeKey(id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	moveType,_ := strconv.Atoi(r.FormValue("Type"))
	moveData := []byte(r.FormValue("Data"))

	userId := r.FormValue("UserId")
	m := Move{
		UserId: userId,
		Timestamp: time.Now(),
		Type: moveType,
		Data: moveData,
	}	

	key, err := datastore.Put(c, datastore.NewIncompleteKey(c, "move", gameKey), &m)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	response := GameMoveResponse{
		Success: true,
		Id: key.Encode(),
	}
	
	data, err := json.Marshal(response)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Write(data)
}

func queueGet(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	userId := r.FormValue("UserId")
	q := datastore.NewQuery("queueplayer").Filter("UserId =", userId).Order("-Timestamp").KeysOnly()
	keys, err := q.GetAll(c, nil)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	gameKeys := make([]*datastore.Key, len(keys))
	for i,k := range keys {
		gameKeys[i] = k.Parent()
	}

	games := make([]Game, len(keys))
	c.Infof("%s", gameKeys)
	err = datastore.GetMulti(c, gameKeys, games)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	for i,k := range keys {
		games[i].Id = k.Parent().Encode()
	}

	data, err := json.Marshal(games)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Write(data)
}
