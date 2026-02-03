from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from typing import List
import yfinance as yf

app = FastAPI()

# ✅ CORS CONFIG (THIS IS THE FIX)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],          # allow frontend
    allow_credentials=True,
    allow_methods=["*"],          # allow POST, OPTIONS, etc
    allow_headers=["*"],
)

@app.post("/prices")
def get_prices(symbols: List[str]):
    result = {}

    for symbol in symbols:
        ticker = yf.Ticker(symbol)
        data = ticker.history(period="1d", interval="1m")

        if data.empty:
            result[symbol] = {"error": "No data"}
        else:
            latest = data.iloc[-1]
            result[symbol] = {
                "price": round(float(latest["Close"]), 2)
            }

    return result
