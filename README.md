# PoMa - Portfolio Manager

A comprehensive portfolio management application with AI-powered insights.

## Features

- Portfolio tracking and management
- Real-time price updates
- Price alerts and notifications
- AI-powered insights using Google Gemini API
- Asset allocation tracking
- Transaction history

## Setup

### Prerequisites

- Python 3.8+
- Java 17+
- MySQL Database
- Node.js (for frontend)

### Backend Setup (Java Spring Boot)

1. Navigate to the `backend` directory
2. Configure database connection in `src/main/resources/application.properties`
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend will run on `http://127.0.0.1:8082`

### Price Service Setup (Python FastAPI)

1. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```

2. Set up Gemini API Key:
   - Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Set it as an environment variable:
     ```bash
     # Windows (PowerShell)
     $env:GEMINI_API_KEY="your-api-key-here"
     
     # Windows (CMD)
     set GEMINI_API_KEY=your-api-key-here
     
     # Linux/Mac
     export GEMINI_API_KEY=your-api-key-here
     ```

3. Run the FastAPI service:
   ```bash
   uvicorn main:app --reload --port 8000
   ```
   The service will run on `http://127.0.0.1:8000`

### Frontend Setup

1. Open `index.html` in a web browser or serve it using a local server
2. Ensure both backend services are running

## AI Insights

The AI Insights feature uses Google's Gemini API to provide:
- Portfolio performance analysis
- Asset allocation recommendations
- Market news and trends
- Tax optimization suggestions
- Risk assessment

Insights are automatically refreshed every 5 minutes, or you can manually refresh using the refresh button.

## API Endpoints

### Price Service (Python)
- `POST /prices` - Get current prices for symbols
- `GET /ai-insights` - Get AI-generated portfolio insights

### Backend (Java)
- `GET /api/holdings` - Get all holdings
- `POST /api/transactions/buy` - Create buy transaction
- `POST /api/holdings/sell` - Create sell transaction
- `GET /api/alerts` - Get all alerts
- `POST /api/alerts` - Create price alert
- `GET /api/notifications` - Get notifications

## Notes

- Make sure to keep your Gemini API key secure and never commit it to version control
- The AI insights require an active internet connection to call the Gemini API
- Portfolio data is fetched from the Java backend, so ensure it's running for insights to work properly
