# AI Fitness Assistant

A modern, AI-powered fitness application built with Spring Boot that generates personalized exercises, workouts, and training plans using advanced GPT models. Features a beautiful dark-themed UI with glassmorphism effects and seamless AI integration.

## ✨ Features

### 🤖 AI-Powered Generation
- **Smart Exercise Generation**: AI creates personalized exercises based on muscle groups, difficulty levels, and available equipment
- **Intelligent Workouts**: Generate complete workout routines with proper exercise sequencing, sets, reps, and rest periods
- **Comprehensive Training Plans**: Build multi-week training programs with progressive overload and recovery planning

### 🎨 Modern UI/UX
- **Dark Theme**: Beautiful dark-themed interface that's easy on the eyes
- **Glassmorphism Effects**: Modern frosted glass design elements throughout
- **Custom Dropdowns**: Smooth, animated dropdown menus with glassmorphism styling
- **Responsive Design**: Works perfectly on desktop, tablet, and mobile devices
- **Interactive Elements**: Hover effects, smooth animations, and visual feedback

### 🏗️ Technical Features
- **Dual AI Integration**: Support for both Chat2API (free) and OpenAI API (paid)
- **Automatic Process Management**: Chat2API server starts/stops automatically with the app
- **RESTful API**: Complete JSON API for integration with other applications
- **Database Persistence**: MySQL database with JPA/Hibernate ORM
- **Comprehensive Error Handling**: User-friendly error messages and validation

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java 17+** (for Spring Boot)
- **Maven 3.6+** (or use included Maven wrapper)
- **MySQL 8.0+** (database server)
- **Python 3.8+** (for Chat2API - optional, auto-downloaded)
- **Git** (for cloning Chat2API - optional, auto-handled)

## 🚀 Setup & Installation

### Database Setup

1. **Install MySQL** and create a database:
```sql
CREATE DATABASE workout_gen_svc;
```

2. **Configure database connection** in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/workout_gen_svc
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### Option 1: Chat2API (Free, Recommended)

Chat2API provides **free unlimited GPT-3.5 access** and GPT-4 with your ChatGPT account.

#### Automatic Setup (Easiest)
```bash
# Just run your Spring Boot app - Chat2API downloads automatically!
./mvnw spring-boot:run
```
**The app will automatically download and install Chat2API if not found!**

#### Manual Setup (If needed)
```bash
# Install Chat2API manually
git clone https://github.com/Niansuh/chat2api.git
cd chat2api
pip install -r requirements.txt

# Start Chat2API
python app.py
```

#### Setup Script (Windows)
```bash
# Run this once to install Chat2API
setup-chat2api.bat
```

Your Spring Boot app will automatically detect and use Chat2API!

### Option 2: OpenAI API (Paid)

If you prefer to use official OpenAI API:

Add your OpenAI API key to `src/main/resources/application.properties`:

```properties
openai.api.key=your-openai-api-key-here
openai.api.model=gpt-3.5-turbo  # or gpt-4
chat2api.auto-start=false      # Disable Chat2API
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` with:
- ✅ **Modern Web UI** at `http://localhost:8080` (dark theme, glassmorphism effects)
- ✅ **REST API** at `http://localhost:8080/api/v1/ai/*`
- ✅ **Automatic Chat2API setup** (downloads and starts if not present)
- ✅ **MySQL database** auto-initialization

### Chat2API Configuration

Configure Chat2API behavior in `application.properties`:

```properties
# Chat2API Process Management
chat2api.path=${CHAT2API_PATH:chat2api}           # Directory containing Chat2API
chat2api.command.start=${CHAT2API_START_COMMAND:start-chat2api.bat} # Command to start Chat2API
chat2api.command.setup=${CHAT2API_SETUP_COMMAND:setup-chat2api.bat} # Command to setup Chat2API
chat2api.port=${CHAT2API_PORT:5005}               # Chat2API port

# Chat2API Connection
chat2api.base-url=${CHAT2API_BASE_URL:http://localhost:5005}
chat2api.access-token=${CHAT2API_ACCESS_TOKEN:}   # Your ChatGPT access token
```

### 🔄 Sharing with Others

When sharing this project:

#### **Required Files**
- ✅ **Source code**: Complete Java Spring Boot application
- ✅ **Chat2API setup**: `setup-chat2api.bat` and `start-chat2api.bat` scripts
- ✅ **Documentation**: `AI_README.md`, `architecture-diagram.txt`
- ✅ **Dependencies**: `pom.xml` with all required dependencies

#### **Setup Instructions for Recipients**
1. **Database Setup**: Create MySQL database and configure connection
2. **Environment Variables**:
   ```bash
   # Set your ChatGPT access token (optional but recommended)
   set CHAT2API_ACCESS_TOKEN=your_chatgpt_token_here

   # Or use OpenAI API directly
   # Add to application.properties: openai.api.key=your_openai_key
   ```
3. **First Run**: The app will automatically download and setup Chat2API
4. **Access**: Visit `http://localhost:8080` for the modern web interface

#### **Key Features to Highlight**
- 🎨 **Modern UI**: Dark theme with glassmorphism effects
- 🤖 **Dual AI Support**: Chat2API (free) or OpenAI API (paid)
- 📱 **Responsive Design**: Works on all devices
- 🔌 **REST API**: Complete programmatic access
- 🗄️ **Database Persistence**: MySQL with automatic schema management

## 🔌 API Endpoints

### Generate Single Exercise
```http
POST /api/v1/ai/exercises
```

**Parameters:**
- `muscleGroup` (required): Target muscle group (e.g., "Chest", "Back", "Legs")
- `difficulty` (optional): "Beginner", "Intermediate", "Advanced" (default: "Intermediate")
- `equipment` (optional): Available equipment (default: "Bodyweight")

**Example:**
```bash
curl -X POST "http://localhost:8080/api/v1/ai/exercises?muscleGroup=Chest&difficulty=Intermediate&equipment=Dumbbells"
```

### Generate Workout
```http
POST /api/v1/ai/workouts
```

**Parameters:**
- `type` (required): Workout type (e.g., "Strength", "Cardio", "HIIT")
- `duration` (optional): Duration in minutes (default: "45")
- `fitnessLevel` (optional): "Beginner", "Intermediate", "Advanced"
- `goals` (required): Fitness goals (e.g., "Build Muscle", "Lose Weight", "Improve Endurance")

### Generate Training Plan
```http
POST /api/v1/ai/plans
```

**Parameters:**
- `duration` (optional): Plan duration in weeks (default: "4")
- `frequency` (optional): Days per week (default: "3")
- `goals` (required): Fitness goals
- `experience` (optional): Experience level (default: "Intermediate")

## Response Format

All endpoints return JSON responses with the generated entities. For example:

```json
{
  "id": "uuid-here",
  "name": "AI Generated Push-ups",
  "type": "STRENGTH",
  "muscleGroupTarget": [],
  "createdAt": "2025-11-30T..."
}
```

## Error Handling

The API includes comprehensive error handling:
- **400 Bad Request**: Invalid parameters
- **500 Internal Server Error**: AI generation failures or server errors

Error responses include detailed error messages and error types.

## 📚 Documentation & Architecture

### 📖 Complete Documentation
- **`docs/WorkoutGenSvc-GenAI-Documentation.docx`** - Comprehensive project documentation
- **`architecture-diagram.txt`** - Detailed system architecture diagram
- **`HELP.md`** - Additional help and troubleshooting

### 🏗️ System Architecture

The application follows a clean layered architecture:

#### **Presentation Layer**
- **Thymeleaf Templates**: Modern dark-themed UI with glassmorphism effects
- **Custom Components**: Animated dropdowns, responsive design, interactive elements

#### **Application Layer (Controllers)**
- **AppWebController**: Web page endpoints with form handling
- **AppRestController**: REST API endpoints with JSON responses

#### **Business Logic Layer (Services)**
- **AIService**: AI prompt engineering and API communication
- **AIProcessManager**: Automatic Chat2API server lifecycle management
- **ExerciseService**: Exercise generation and validation
- **WorkoutService**: Workout creation with exercise relationships
- **PlanService**: Training plan generation with lazy loading optimization

#### **Data Access Layer (Repositories)**
- **JPA/Hibernate**: ORM with optimized queries and lazy loading
- **MySQL Database**: Persistent storage with automatic schema management

#### **External Services**
- **Chat2API**: Local Python service providing free GPT access
- **OpenAI API**: Direct API access (paid alternative)

### 🔄 Data Flow
```
Client → Controller → Service → AI Service → Chat2API/OpenAI → Database → Client
```

### 🛡️ Key Technical Features
- **Automatic AI Service Management**: Chat2API downloads and starts automatically
- **Lazy Loading Optimization**: Complex entity graphs loaded efficiently
- **Transaction Management**: Proper database transaction handling
- **Error Handling**: Comprehensive validation and user-friendly error messages
- **Modern UI**: Dark theme, glassmorphism, smooth animations

## Security Considerations

- Store your OpenAI API key securely (never commit to version control)
- Consider implementing rate limiting for API endpoints
- Add authentication/authorization as needed for production use

## Cost Optimization

### With Chat2API (Free):
- ✅ **Unlimited GPT-3.5 usage** - No API costs
- ✅ **GPT-4 access** using your ChatGPT subscription
- ✅ **No rate limits** for basic usage

### With OpenAI API (Paid):
- AI requests consume OpenAI API credits
- Consider caching frequently requested content
- Implement request validation to prevent unnecessary API calls

### General Optimizations:
- Cache frequently generated content
- Implement request validation
- Use appropriate model sizes (GPT-3.5-turbo for most cases)

## 🎯 Recent Updates & Features

- ✅ **Modern Dark UI**: Complete redesign with glassmorphism effects
- ✅ **Custom Dropdowns**: Smooth animated dropdown menus
- ✅ **Enhanced Training Plans**: Detailed week-by-week workout display
- ✅ **Improved Error Handling**: Comprehensive validation and user feedback
- ✅ **Automatic AI Setup**: Chat2API downloads and configures automatically
- ✅ **Lazy Loading Optimization**: Efficient database queries for complex data
- ✅ **Transaction Management**: Proper database consistency
- ✅ **REST API**: Complete programmatic access with JSON responses

## 🚀 Future Enhancements

### **User Experience**
- 👤 **User Accounts**: Personal profiles with preferences and history
- 📊 **Progress Tracking**: Workout completion and performance analytics
- 🎯 **Goal Setting**: Personalized fitness goal management
- 📱 **Mobile App**: Native mobile application
- 🌍 **Multi-language**: Internationalization support

### **AI & Content**
- 🎨 **Exercise Media**: AI-generated images and videos for exercises
- 🧠 **Adaptive Plans**: Dynamic plan adjustments based on progress
- 💬 **AI Chat**: Conversational fitness coaching
- 📈 **Performance Analytics**: Advanced workout analytics and insights

### **Integration**
- 🏃 **Fitness Devices**: Integration with wearables and fitness trackers
- 📅 **Calendar Sync**: Workout scheduling with external calendars
- 🏥 **Health Integration**: Medical data and health record integration
- 🥗 **Nutrition Plans**: AI-generated meal plans and nutrition guidance

### **Technical**
- 🔄 **Real-time Sync**: Live workout data synchronization
- 📊 **Advanced Analytics**: Machine learning-powered insights
- 🔒 **Enhanced Security**: OAuth, JWT, and advanced authentication
- ⚡ **Performance**: Caching, CDN, and performance optimizations
