# PopularMovies-2
Udacity Android NanoDegree Project - Popular Movies Stage 2

In Stage 2 of Popular Movies, additional features has been implemented to enhance the experience of the app.

- User is now able to view and play trailers of the movie.
- User is now able to read reviews of a selected movie.
- User can now mark a movie as a favorite in the details view by tapping a heart button.
  When a movie is marked as a "favorite", the movie information gets stored in a local database.
  So, even when there is no internet connection, users can see their favorites movies.
- Existing sorting criteria has been modified to include an additional pivot to show their favorites collection.

- Also, app experience has been optimized for tablet. Tablet UI uses a Master-Detail layout implemented using fragments.
  The left fragment is for discovering movies. The right fragment displays the movie details view for the currently selected movie.


[![Screen Record on youtube](http://img.youtube.com/vi/Vgtzl-KnaPs/0.jpg)](https://www.youtube.com/watch?v=Vgtzl-KnaPs)


Tablet UI using Master-Detail Layout
![tablet_ui_master_detail](https://udacity-github-sync-content.s3.amazonaws.com/_imgs/7402/1466036925/Screenshot_20160615-201656.png)

Favorite Movies from Local Database
![favorite_movies_from_database](https://udacity-github-sync-content.s3.amazonaws.com/_imgs/7402/1466036925/Screenshot_20160615-201755.png)


Important Note : Please add the API_KEY in DataManager class inside network package before running the app.
  
  
