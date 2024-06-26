# StoryPlayer Android Project

This repository contains an Android application for displaying stories similar to Instagram.

You can watch my demo at that [link](https://drive.google.com/file/d/1P9CcWeYlI-YUQ18kNw8Vjno8vUHOaclM/view?usp=sharing)

<details><summary><h2> Project Requirements</h2></summary>

Create a UI component which will be a very simple version of Instagram’s Story Player.
The followings are what your module needs to be able to do.
1. Cubic transition among story groups.
2. Pause the story immediately when the users rest and hold, continue when lifted.
3. Go to the previous story and/or story group when users tapped left.
4. Go to the next story and/or story group when users tapped right.
5. Story media could be dummy images and videos on the internet to be downloaded by the player.
6. Duration for images is 5 seconds.
7. Durations for videos are their content length.
8. Each story in a story group should have an individual progress bar on top of the screen which arranges itself according to its duration.
9. While users swiping among story groups, story groups should start from the story they left off unless they are seeing for the first time.

</details>

<details><summary><h2> Opening the Project in Android Studio</h2></summary>


To open the project in Android Studio, follow these steps:

1. Clone the repository to your local machine.
2. Open Android Studio.
3. On the Welcome screen, click "Open an Existing Project".
4. Navigate to the cloned repository directory.
5. Open the `StoryPlayer` subdirectory as the project root.

6. You can follow the images to switch to the Android Scope also

<details><summary><h3> Screen Shots </h3></summary>
<img width="778" alt="Screenshot 2024-04-25 at 10 23 56" src="https://github.com/egecans/StoryPlayer/assets/87999176/57698548-ae54-4b87-8709-030d9f050ae1">
<img width="1393" alt="Screenshot 2024-04-25 at 11 11 51" src="https://github.com/egecans/StoryPlayer/assets/87999176/e54267c6-e267-4eb6-aa38-529257f6c934">
<img width="425" alt="Screenshot 2024-04-25 at 10 32 09" src="https://github.com/egecans/StoryPlayer/assets/87999176/fa068266-77a5-4419-b5ae-766420102412">
</details>


<details><summary><h3> If it couldn't work somehow you should do Invalidate caches </h3></summary>
<img width="1434" alt="Screenshot 2024-04-25 at 11 08 12" src="https://github.com/egecans/StoryPlayer/assets/87999176/4436c759-52c9-41f4-b606-71ead904316b">
<img width="781" alt="Screenshot 2024-04-25 at 11 08 55" src="https://github.com/egecans/StoryPlayer/assets/87999176/07dd091b-0195-411d-bb04-7e8da4e9ca4d">
<img width="1439" alt="Screenshot 2024-04-25 at 11 12 50" src="https://github.com/egecans/StoryPlayer/assets/87999176/5ec66d11-cacc-4c89-9668-80d1d12a1716">

</details>

<details><summary><h3> If your device is Android 11 or higher, select always install with package manager </h3></summary>
<img width="315" alt="Screenshot 2024-04-25 at 11 20 05" src="https://github.com/egecans/StoryPlayer/assets/87999176/46cdfb05-9577-4a36-8fd0-bf56a804a399">
<img width="1031" alt="Screenshot 2024-04-25 at 11 20 22" src="https://github.com/egecans/StoryPlayer/assets/87999176/39f7e558-554b-4779-bb1c-fe397acff632">
</details>

</details>

<details><summary><h2> Installing apk on your android device directly </h2></summary>

Here is the [apk](https://help.esper.io/hc/en-us/articles/12657625935761-Installing-the-Android-Debug-Bridge-ADB-Tool)

If you have an android device, open its developer mode, and connect to your computer. Then you should install the apk by adb install. With the following command:

adb install <path_to_app-debug.apk> 

If your device doesn't have adb you should install adb first by the [following link](https://help.esper.io/hc/en-us/articles/12657625935761-Installing-the-Android-Debug-Bridge-ADB-Tool)

</details>
