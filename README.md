# JumpGame
Run and Jump game made using JavaFX

---------- IMPLEMENTATION ----------
The code for this game is written in Java 8 to create an application using JavaFX, using Intellij Idea 2017.2.4.
Time delays were implemented using java.util.Timer and java.util.TimerTask and java.util.Date.
The game's level implementation is using a 12 X 60 matrix, with each matrix element corresponding to a 60 x 60 pixels block.
All elements of the level as created using javafx.scene.shape.Rectangle and are as follows:
>user character is a 40 x 40 pixel green Rectangle
>platforms are dark blue Rectangles(60X60 pixels) which are concatenated together.
>obstacles are orange Rectangles(60x60 pixels)

---------- GAME DESCRIPTION ----------
The game takes 3 user inputs: Left Arrow Key to move back, Spacebar to jump and Right Arrow Key to go forward.
The game is ran directly from the executable jar file, with a help screen which appears detailing the instructions for a few seconds.
Then the game begins. The user charactercan jump, fall and move back and forward. 
The character dies if it falls between the gaps of the platform and also if it hits any obstacles.
If user reaches end of level he wins.
On death a screen is displayed with a "You Lose" message and the application automatically closes.
On winning a "You win Screen is displayed and the application automatically closes.
To play again, you have to open it from executable jar file.

---------- USAGE INSTRUCTIONS ----------

Download all files in the repository
Bulid an executable jar file using the code, or use the one provided in the repository to run the application.
