package game

import (
	"fmt"
	"net/http"
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
	http.HandleFunc("/api/start", start)
	http.HandleFunc("/api/move", move)
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

func start(w http.ResponseWriter, r *http.Request) {
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

	fmt.Fprintf(w, "%s", key.Encode())
}

func move(w http.ResponseWriter, r *http.Request) {
	u := checkLogin(w, r)
	fmt.Fprintf(w, "move, %v!", u)
}