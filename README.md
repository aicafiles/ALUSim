# ALUSim

## About
ALUSim is a desktop application that visually simulates an Arithmetic Logic Unit (ALU). With an intuitive interface, it lets users perform and visualize basic arithmetic and logic operations perfect for students, educators, or anyone learning about digital logic and computer architecture.

## Key Features
- Perform arithmetic operations (Add, Subtract, Multiply) 
- Perform logic operations (AND, OR, NOT)
- Supports both decimal and binary input modes
- Real-time binary and decimal conversion display
- Error handling and input validation

## Tech Stack
- Java (Swing for GUI)
- No external dependencies required

## Getting Started
1. Clone or download this repository
2. Navigate to the project directory
3. Run the application

## Usage Notes
- Enter numbers in the input fields (choose decimal or binary mode).
- Select the desired operation from the dropdown.
- Click "Calculate" to see the result and its binary representation.
- View and manage your calculation history in the side panel.
- Error dialogs will appear for invalid input or unsupported operations.

## Project Structure
- `Source/Alu.java` — Main GUI and application logic
- `Source/Logic.java` — Core ALU operations and history management
- `Source/Ui.java` — UI component styling and helpers
- `Source/Main.java` — Application entry point
