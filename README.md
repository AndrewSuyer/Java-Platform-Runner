# Java-Platform-Runner
A platformer game I made for my final project in AP computer science A, which I took in junior year. This project is written entirely in Java and uses the swing library for graphics.

_The game uses textures that I had stored locally on my computer, so, textures may not load properly if you try to download and play this game on your own computer._

## Features
  - 2 "worlds" each containing 4 levels for a total of 8 levels.
  - Realistic player movement using kinematics equations
  - Animated player movements (walking, jumping, squatting)
  - 6 types of blocks (solid, breakable, transparent, deadly, background, and other)

## Pictures
![image](https://github.com/AndrewSuyer/Java-Platform-Runner/assets/118581166/6580594e-0d53-476d-a1a7-f8568d56e44b)
> The world and level selection screen. This is the first screen you see when you start the game


![image](https://github.com/AndrewSuyer/Java-Platform-Runner/assets/118581166/1249f4ae-6782-47f4-bbe6-a4d655aded8b)
> A part of world 1 level 2, an elaborate brick structure floating way up in the air


![image](https://github.com/AndrewSuyer/Java-Platform-Runner/assets/118581166/82d9dff6-ed8f-4827-8082-6ed5bcb5c6d4)
> A part of world 2 level 4, a cave themed level. The player has just landed on a spike and died!

## How its made
The most interesting part of the development of this game is the level creation. If you looked into the code, you would notice every block of every level is placed manually. I obviously didnt write the thousands of lines of code manually. Instead, I used google sheets to plan out each level, then I used a script to generate the code. 

Each block in the game is represented by a color in google sheets. When creating a level, I would draw it out (literally) in google sheets using the colors I defined. Here are some pictures of what that looked like:

![Screenshot_20230922_153657](https://github.com/AndrewSuyer/Java-Platform-Runner/assets/118581166/f4fe25b3-02f1-4929-b638-84982caaaf4b)
