# Dandy
Dandy is a spotify wrapper with some changes and adjustments. This project was created in order for me to play around with new libraries, tools, architectures and apply best practises. The app is still in development so it has some flaws and not every window/feature is implemented.
//TODOS
• Completely rector from MVP to MVVM and change unit tests accordingly
• Change dagger 2 architecture to achieve better code reusability
• Spotify services sometimes sometimes produce HTTP 429 exception so exponential backoff should be implemented
• Implement overall error handling
• Because Spotify Android playback SDK is extremely buggy I wrote the logic for playback managing myself and it needs refactoring (PlayerPresenter.kt and PlayerFragment.kt)
• Package structure needs to be refactored
• More test coverage
• Orientation change support
• Overall refactoring


