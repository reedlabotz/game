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
	UserId string
	Type int
	Data []byte
	Timestamp time.Time
}

type Game struct {
	UserId string
	Started time.Time
  Players []string
}

type QueuePlayer struct {
	UserId string
	Timestamp time.Time
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

type GameStartResponse struct {
	Success bool
	Id string
}

func gameStart(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	userId := r.FormValue("UserId")
	players := append(strings.Split(r.FormValue("Players"),","), userId)
	
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
		response := GameStartResponse{
			Success: true,
			Id: key.Encode(),
		}
		
		data, err := json.Marshal(response)
		if err != nil {
			return err
		}
		w.Write(data)
		
		return nil
	}, nil)
	
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
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

type QueueGetResponse struct {
	Games []string
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
	games := make([]string, len(keys))
	for i,k := range keys {
		games[i] = k.Parent().Encode()
	}
	response := QueueGetResponse{
		Games: games,
	}

	data, err := json.Marshal(response)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Write(data)
}
