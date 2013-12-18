package game

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"time"
	
	"appengine"
	"appengine/datastore"
	"appengine/user"
)

type Move struct {
	Owner string
	Data string
	Type int
	Timestamp time.Time
}

type Game struct {
	Owner string
	Started time.Time
}

func init() {
	http.HandleFunc("/alive", alive)
	http.HandleFunc("/api/game/start", gameStart)
	http.HandleFunc("/api/game/get", gameGet)
	http.HandleFunc("/api/game/move", gameMove)
}

func checkLogin(w http.ResponseWriter, r *http.Request) *user.User {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, err := user.LoginURL(c, r.URL.String())
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return nil
		}
		w.Header().Set("Location", url)
		w.WriteHeader(http.StatusFound)
		return nil
	}
	return u
}

func alive(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "yes")
}

type GameStartResponse struct {
	Success bool
	Id string
}

func gameStart(w http.ResponseWriter, r *http.Request) {
	u := checkLogin(w, r)
	c := appengine.NewContext(r)

	g := Game{
		Owner: u.String(),
		Started: time.Now(),
	}
	key, err := datastore.Put(c, datastore.NewIncompleteKey(c, "game", nil), &g)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	response := GameStartResponse{
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

type GameGetResponse struct {
	LastMove Move
}

func gameGet(w http.ResponseWriter, r *http.Request) {
	checkLogin(w, r)
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
		LastMove: moves[0],
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
	u := checkLogin(w, r)
	c := appengine.NewContext(r)

	id := r.FormValue("Id")

	gameKey, err := datastore.DecodeKey(id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	moveType,_ := strconv.Atoi(r.FormValue("MoveType"))
	moveData := r.FormValue("MoveData")

	m := Move{
		Owner: u.String(),
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