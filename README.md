### Dandy

Dandy is a Spotify client with some changes and adjustments. This project was created in order for me to play around with new libraries, tools, architectures and apply best practises. The app is still in development so it has some flaws and not every window/feature is implemented.

### TODOS

- Completely refactor from MVP to MVVM and change unit tests accordingly.
-  Change dagger 2 architecture to achieve better code reusability.
- Spotify services sometimes sometimes produce HTTP 429 exception so exponential backoff should be implemented.
- Implement overall error handling.
-  Because Spotify Android playback SDK is extremely buggy I wrote the logic for playback managing myself and it needs refactoring (PlayerPresenter.kt and PlayerFragment.kt).
- Package structure needs to be refactored.
- More test coverage.
- Orientation change support.
- Implement access token refreshing feature.
- Playback is extremelly buggy. (This will be fixed in near future)
- Overall refactoring.

## Artist
![artist_fragment](https://user-images.githubusercontent.com/18017952/43677376-ab9d3f82-9809-11e8-8c81-3e5d6b2f05b5.png)
![albums](https://user-images.githubusercontent.com/18017952/43677375-ab804d50-9809-11e8-9012-7533f62d0c1c.png)
![artists_albums](https://user-images.githubusercontent.com/18017952/43677377-abb7576e-9809-11e8-92d9-d2152ef46b81.png)
![similar](https://user-images.githubusercontent.com/18017952/43677382-ac3a12d0-9809-11e8-88f2-2fe8872ddd8e.png)
## Home
![home](https://user-images.githubusercontent.com/18017952/43677378-abd42ea2-9809-11e8-843e-d21aef0bb064.png)
## Likes
![likes_artists](https://user-images.githubusercontent.com/18017952/43677379-abed7876-9809-11e8-82e1-25d1ff52b275.png)
![likes_tracks](https://user-images.githubusercontent.com/18017952/43677380-ac068c12-9809-11e8-8a1c-487203aa042e.png)
## Player
![player](https://user-images.githubusercontent.com/18017952/43677381-ac200eb2-9809-11e8-93e3-a6d9c6184de6.png)


