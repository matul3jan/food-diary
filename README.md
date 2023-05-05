## [Demo video here](https://www.youtube.com/watch?v=Xyv-ZhZviQM)

###  Introduction

The Food Diary app is a mobile application that helps users plan, save, and share their food experiences. The app uses the Google Maps API to display and explore nearby food options, allowing users to search for specific restaurants or cuisines.

<hr>

### Functionality

| Feature       | Description                                                                                                                                                                    | Tool                                |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|
| Splash screen | The splash screen welcomes users to the app with a visually appealing interface.                                                                                               | -                                   |
| Auth          | The user login/registration system provides an authentication mechanism for users to create an account or sign in to an existing one using their Google account.               | Firebase Auth (Email + Google)      |
| Persistence   | The app will save user data, such as the food destinations they have visited, the dishes they tried, and their ratings, so they can revisit their experiences at a later time. | Firebase (Database + Cloud Storage) |
| CRUD          | Users can create, read, update, and delete their food entries, which will be displayed as a list.                                                                              | -                                   |
| Maps          | The app uses the Google Maps API to display the location of nearby food destinations and also highlights those with experiences from both self and other users.                | Google Maps                         |
| Dark mode     | Users can switch between dark and light modes in the app.                                                                                                                      | -                                   |

<hr>

### UML Class Diagram

<img width="1420" alt="image" src="https://user-images.githubusercontent.com/26350749/236260385-13e6c608-11d5-4be1-a67f-ca09fb8d2464.png">

<hr>

### UX Approach

The User Experience approach adopted in the Food Diary app's development focused on ensuring that the app was user-friendly and easy to use. Following Material design guidelines ensured that lot of emphasis on the app's design and visual appeal, ensuring that it was aesthetically pleasing while remaining functional. We also made sure that the app was intuitive and easy to navigate, with clear labels and icons to guide the user.

<hr>

### DX Approach

Food Diary app was developed using the MVVM (Model-View-ViewModel) pattern. The MVVM pattern is a widely used software architecture pattern that separates the presentation logic from the business logic of an application.

It simplifies the application's structure by breaking it down into three distinct parts: Model, View, and ViewModel. This separation makes it easier to test and maintain the application, as each component can be developed independently.

##### Model

Business logic of the application, which includes the data and how it is stored.

##### View

Presentation of the data to the user, including the UI (User Interface) and any user interaction.

##### ViewModel

Intermediary between the View and the Model, handling user input and updating the View accordingly.

<hr>

### Git Approach

Below git approach allowed for a structured and organized development process, with separate branches for development and production, and a clear process for code review and issue tracking. This approach also made it easy to track changes over time and maintain a clean and stable codebase, while also allowing for continuous improvement and bug fixing.

- Two branches were used: the main branch (master) and a development branch (dev).
- For each new feature or bug, a new issue was created in the issue tracker, and assigned to the appropriate team member.
- The developer would then checkout the dev branch and start working on the assigned issue, committing changes to the dev branch as necessary.
- Once the feature or bug was fully implemented and tested, a pull request (PR) was created to merge the dev branch into the master branch.
- The PR was reviewed by another team member, who could approve the changes or suggest further revisions.
- Once the review was complete, the PR was merged and closed, and the linked issue was marked as resolved.
- Once a working set of features had been implemented, a tagged release of the application was created on the master branch.
- If any bugs were found after release, they were treated as new issues and a similar approach was followed to fix them.
- Once the bugs were fixed and tested, a new tagged release of the application was created.

<hr>

### Personal statement

Developing the Food Diary app was a challenging yet fulfilling experience for me, as I worked on it alone and learned a lot about Android app development. I had no prior experience in mobile app development, but I was able to learn and implement the necessary skills, including working with the Google Maps API and utilizing the MVVM pattern. Throughout the development process, I relied on Git for version control and found it to be an invaluable tool for managing my codebase. I also encountered an issue while implementing the feature to capture images from the camera within the application, but I was able to resolve it with the help of the Stack Overflow community.

Additionally, I recognize that there is always room for improvement and growth. While I was able to implement a number of key features in the app, such as the ability for users to log their food and experiences, I unfortunately was not able to fully implement a social element where users could comment on experiences shared by other users. This is due to limitation of time and other work on other modules.

Overall, developing the Food Diary app was an enriching journey that allowed me to grow as a developer and gain valuable experience in mobile app development.

<hr>

### References

- [Night mode for Google maps?](https://stackoverflow.com/questions/39191867/night-mode-for-google-maps)
- [Take picture and save it on internal storage in Android](https://stackoverflow.com/questions/59852426/take-picture-and-save-it-on-internal-storage-in-android)
- [Capture Image from Camera and Display in Activity](https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity)
- [When I press back button and reopen my app it goes back to login screen but user is still logged in](https://stackoverflow.com/questions/71988351/when-i-press-back-button-and-reopen-my-app-it-goes-back-to-login-screen-but-user)
- [How to pass a variable from Activity to Fragment, and pass it back?](https://stackoverflow.com/questions/17436298/how-to-pass-a-variable-from-activity-to-fragment-and-pass-it-back)
