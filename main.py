from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from typing import List, Optional
import yfinance as yf
from google import genai
import os
import httpx
import json
from datetime import datetime
from dotenv import load_dotenv

print("🔥 FASTAPI MAIN.PY LOADED 🔥")

app = FastAPI()

# ✅ CORS CONFIG (THIS IS THE FIX)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],          # allow frontend
    allow_credentials=True,
    allow_methods=["*"],          # allow POST, OPTIONS, etc
    allow_headers=["*"],
)

load_dotenv()  # loads .env into os.environ

# Gemini API Configuration
# GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")
# if GEMINI_API_KEY:
#     genai.configure(api_key=GEMINI_API_KEY)
# print("Gemini key loaded:", bool(GEMINI_API_KEY))
GEMINI_API_KEY="your_api_key"
client = genai.Client(api_key=GEMINI_API_KEY)

# Java Backend URL
JAVA_BACKEND_URL = "http://127.0.0.1:8080"

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

@app.get("/ai-insights")
async def get_ai_insights():
    """
    Generate AI insights based on portfolio holdings and market data.
    """
    print("🔥 /ai-insights called")

    if not GEMINI_API_KEY:
        return {
            "insights": [
                "⚠️ Gemini API key not configured.",
                "Set GEMINI_API_KEY in environment variables."
            ]
        }

    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            holdings_response = await client.get(f"{JAVA_BACKEND_URL}/api/holdings")
            holdings_response.raise_for_status()
            holdings = holdings_response.json()

        if not holdings:
            return {
                "insights": [
                    "📊 Your portfolio is empty.",
                    "Add assets to get AI insights."
                ]
            }

        symbols = [h["asset"]["symbol"] for h in holdings]

        # ✅ Direct call, no HTTP
        prices_data = get_prices(symbols)

        portfolio_summary = build_portfolio_summary(holdings, prices_data)

        insights = await generate_insights_with_gemini(
            portfolio_summary,
            holdings,
            prices_data
        )

        return {"insights": insights}

    except Exception as e:
        print(f"Error generating insights: {e}")
        return {
            "insights": [
                "⚠️ Error generating AI insights.",
                str(e)
            ]
        }

def build_portfolio_summary(holdings, prices_data):
    """Build a comprehensive portfolio summary for AI context."""
    total_invested = 0
    total_market_value = 0
    asset_type_breakdown = {}
    profit_loss_by_asset = []
    best_performer = None
    worst_performer = None
    
    for holding in holdings:
        asset = holding["asset"]
        symbol = asset["symbol"]
        asset_type = asset["type"]
        quantity = float(holding["quantity"])
        avg_buy_price = float(holding["avgBuyPrice"])
        
        invested = quantity * avg_buy_price
        total_invested += invested
        
        current_price = prices_data.get(symbol, {}).get("price", float(holding.get("currentPrice", avg_buy_price)))
        market_value = quantity * current_price
        total_market_value += market_value
        
        profit_loss = market_value - invested
        profit_loss_pct = (profit_loss / invested * 100) if invested > 0 else 0
        
        # Asset type breakdown
        if asset_type not in asset_type_breakdown:
            asset_type_breakdown[asset_type] = {"invested": 0, "value": 0, "count": 0}
        asset_type_breakdown[asset_type]["invested"] += invested
        asset_type_breakdown[asset_type]["value"] += market_value
        asset_type_breakdown[asset_type]["count"] += 1
        
        # Track individual asset performance
        profit_loss_by_asset.append({
            "symbol": symbol,
            "name": asset.get("name", symbol),
            "type": asset_type,
            "profit_loss": profit_loss,
            "profit_loss_pct": profit_loss_pct,
            "quantity": quantity
        })
        
        # Track best/worst performers
        if best_performer is None or profit_loss_pct > best_performer["profit_loss_pct"]:
            best_performer = {
                "symbol": symbol,
                "name": asset.get("name", symbol),
                "profit_loss_pct": profit_loss_pct,
                "profit_loss": profit_loss
            }
        
        if worst_performer is None or profit_loss_pct < worst_performer["profit_loss_pct"]:
            worst_performer = {
                "symbol": symbol,
                "name": asset.get("name", symbol),
                "profit_loss_pct": profit_loss_pct,
                "profit_loss": profit_loss
            }
    
    total_profit_loss = total_market_value - total_invested
    total_return_pct = (total_profit_loss / total_invested * 100) if total_invested > 0 else 0
    
    return {
        "total_invested": total_invested,
        "total_market_value": total_market_value,
        "total_profit_loss": total_profit_loss,
        "total_return_pct": total_return_pct,
        "asset_type_breakdown": asset_type_breakdown,
        "holdings_count": len(holdings),
        "best_performer": best_performer,
        "worst_performer": worst_performer,
        "profit_loss_by_asset": profit_loss_by_asset
    }

async def generate_insights_with_gemini(portfolio_summary, holdings, prices_data):
    """Generate AI insights using Gemini API."""
    try:
        
        # Build context prompt
        prompt = f"""You are an expert financial advisor AI assistant. Analyze the following portfolio data and provide 3-5 concise, actionable insights (each should be 1-2 sentences max). Focus on:

1. Portfolio performance and risk assessment
2. Asset allocation and diversification recommendations
3. Specific stock/crypto/asset news or trends that might affect holdings
4. Tax optimization opportunities (short-term vs long-term gains)
5. Market conditions and timing suggestions

Portfolio Summary:
- Total Invested: ₹{portfolio_summary['total_invested']:,.2f}
- Current Market Value: ₹{portfolio_summary['total_market_value']:,.2f}
- Total Profit/Loss: ₹{portfolio_summary['total_profit_loss']:,.2f} ({portfolio_summary['total_return_pct']:.2f}%)
- Number of Holdings: {portfolio_summary['holdings_count']}

Asset Type Breakdown:
"""
        
        for asset_type, data in portfolio_summary['asset_type_breakdown'].items():
            allocation_pct = (data['value'] / portfolio_summary['total_market_value'] * 100) if portfolio_summary['total_market_value'] > 0 else 0
            prompt += f"- {asset_type}: {data['count']} holdings, {allocation_pct:.1f}% of portfolio\n"
        
        if portfolio_summary['best_performer']:
            prompt += f"\nBest Performer: {portfolio_summary['best_performer']['name']} ({portfolio_summary['best_performer']['symbol']}) - {portfolio_summary['best_performer']['profit_loss_pct']:.2f}% return\n"
        
        if portfolio_summary['worst_performer']:
            prompt += f"Worst Performer: {portfolio_summary['worst_performer']['name']} ({portfolio_summary['worst_performer']['symbol']}) - {portfolio_summary['worst_performer']['profit_loss_pct']:.2f}% return\n"
        
        prompt += f"""
Top Holdings:
"""
        # Include top 5 holdings by value
        sorted_holdings = sorted(portfolio_summary['profit_loss_by_asset'], 
                               key=lambda x: abs(x['profit_loss']), reverse=True)[:5]
        for holding in sorted_holdings:
            prompt += f"- {holding['name']} ({holding['symbol']}): {holding['quantity']} units, P/L: ₹{holding['profit_loss']:,.2f} ({holding['profit_loss_pct']:.2f}%)\n"
        
        prompt += """
Current Date: """ + datetime.now().strftime("%Y-%m-%d") + """

Provide insights in a clear, professional tone. Each insight should be actionable and relevant to portfolio management. Format as a JSON array of strings, where each string is one insight.

Example format:
["Your equity exposure is high; consider partial profit booking to reduce tax impact.", "TCS has outperformed its sector benchmark by 6.2% this quarter.", "High short-term gains detected — holding longer could reduce capital gains tax."]

Return ONLY the JSON array, no additional text."""
        
        response=client.models.generate_content(model='gemini-2.5-flash', 
    contents=prompt)
        #response = model.generate_content(prompt)
        
        # Parse response
        response_text = response.text.strip()
        
        # Try to extract JSON array from response
        if response_text.startswith('['):
            try:
                insights = json.loads(response_text)
                if isinstance(insights, list) and len(insights) > 0:
                    return insights[:5]  # Limit to 5 insights
            except json.JSONDecodeError:
                pass
        
        # Fallback: split by lines or new insights markers
        lines = [line.strip() for line in response_text.split('\n') if line.strip()]
        insights = []
        for line in lines:
            # Remove markdown formatting, quotes, etc.
            cleaned = line.replace('"', '').replace("'", '').replace('- ', '').replace('* ', '').strip()
            if cleaned and len(cleaned) > 20:  # Filter out very short lines
                insights.append(cleaned)
        
        if insights:
            return insights[:5]
        
        # Ultimate fallback
        return [
            "📊 Portfolio analysis complete. Review your asset allocation regularly.",
            "💡 Consider rebalancing if any asset class exceeds 40% of your portfolio.",
            "📈 Monitor market trends and news related to your top holdings."
        ]
        
    except Exception as e:
        print(f"Error calling Gemini API: {e}")
        # Return fallback insights
        return [
            f"⚠️ AI insight generation encountered an issue: {str(e)}",
            "📊 Review your portfolio allocation and consider diversification.",
            "💡 Monitor your holdings regularly for optimal performance."
        ]

@app.get("/ping")
def ping():
    return {"status": "ok"}
