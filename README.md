# My Assignments

This Android app allows users to fetch and display assignments stored in a MySQL database. The app uses Jetpack Compose for the UI and integrates with a Node.js backend to retrieve assignment data. It also features Material 3 design elements, dynamic theme switching, and various customization options.

## Features

- **Fetch Assignments:** Retrieve assignments from a MySQL database using a Node.js backend.
- **Dynamic UI:** Built with Jetpack Compose and Material 3 design, supporting dynamic colors and themes.
- **Refresh Functionality:** Users can refresh the assignments list manually.
- **Theming:** Toggle between light and dark modes, with support for dynamic colors on Android 12+ devices.
- **Error Handling:** Displays a message if the server is not reachable.

## Screenshots
nothing yet
<!-- Include screenshots of your app here -->

## Getting Started

### Prerequisites

- **Android Studio**: Latest stable version recommended.
- **Python3**: For running the backend server.
- **MySQL**: Database to store the assignments data.

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/nanerbs25/SchoolAssignments.git
   cd SchoolAssignments
   ```

2. **Setup the backend server:**
   - Coming soon

### Usage

- **Home Screen:** Displays a list of assignments fetched from the database.
- **Refresh:** Tap the refresh button on the top bar to reload assignments.
- **Theme Selection:** Access the theme selection dialog from the settings to switch between light, dark, and dynamic themes.

## API Endpoints

- **GET /assignments:** Fetch all assignments from the database.
- **POST /uploadassignments:** Upload new assignments with title, description, and up to 10 images.

## Technologies Used

- **Kotlin**: For Android app development.
- **Jetpack Compose**: For modern, declarative UI design.
- **Material 3**: For UI components and theming.
- **Node.js**: Backend server to handle API requests.
- **MySQL**: Database to store assignments data.

## Contributing

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add your feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

- **Your Name** - [Nanerbssucks](mailto:nanerbsucks@gmail.comcom)
- **GitHub** - [nanerbs25](https://github.com/nanerbs25)
