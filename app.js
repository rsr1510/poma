const API_URL = "http://127.0.0.1:8000/prices";
const HOLDINGS_API = "http://127.0.0.1:8080/api/holdings";
const ALERTS_API = "http://127.0.0.1:8080/api/alerts";
const NOTIFICATIONS_API = "http://127.0.0.1:8080/api/notifications";
const AI_INSIGHTS_API = "http://127.0.0.1:8000/ai-insights";
const REFRESH_INTERVAL = 15000;
const AI_INSIGHTS_REFRESH_INTERVAL = 300000; // 5 minutes

let topStocks = [
  { name: "Reliance Industries", symbol: "RELIANCE.NS" },
  { name: "Apple Inc.", symbol: "AAPL" },
  { name: "Tata Consultancy Services", symbol: "TCS.NS" },
  { name: "Infosys", symbol: "INFY.NS" },
  { name: "HDFC Bank", symbol: "HDFCBANK.NS" },
  { name: "ICICI Bank", symbol: "ICICIBANK.NS" },
  { name: "Bharti Airtel", symbol: "BHARTIARTL.NS" },
  { name: "State Bank of India", symbol: "SBIN.NS" },
  { name: "Kotak Mahindra Bank", symbol: "KOTAKBANK.NS" },
  { name: "Axis Bank", symbol: "AXISBANK.NS" },
  { name: "Larsen & Toubro", symbol: "LT.NS" },
  { name: "ITC", symbol: "ITC.NS" },
  { name: "Hindustan Unilever", symbol: "HINDUNILVR.NS" },
  { name: "Tata Steel", symbol: "TATASTEEL.NS" },
  { name: "NTPC", symbol: "NTPC.NS" },
  { name: "Power Grid Corporation", symbol: "POWERGRID.NS" },
  { name: "Coal India", symbol: "COALINDIA.NS" },
  { name: "Oil and Natural Gas Corporation", symbol: "ONGC.NS" },
  { name: "GAIL (India)", symbol: "GAIL.NS" },
  { name: "Bharat Petroleum Corporation", symbol: "BPCL.NS" },
  { name: "Hindustan Petroleum Corporation", symbol: "HINDPETRO.NS" },
  { name: "Tata Motors", symbol: "TATAMOTORS.NS" },
  { name: "Mahindra & Mahindra", symbol: "M&M.NS" },
  { name: "Maruti Suzuki India", symbol: "MARUTI.NS" },
  { name: "Bajaj Auto", symbol: "BAJAJ-AUTO.NS" },
  { name: "Hero MotoCorp", symbol: "HEROMOTOCO.NS" },
  { name: "Eicher Motors", symbol: "EICHERMOT.NS" },
  { name: "UltraTech Cement", symbol: "ULTRACEMCO.NS" },
  { name: "Shree Cement", symbol: "SHREECEM.NS" },
  { name: "Grasim Industries", symbol: "GRASIM.NS" },
  { name: "Ambuja Cements", symbol: "AMBUJACEM.NS" },
  { name: "JSW Steel", symbol: "JSWSTEEL.NS" },
  { name: "Tata Power", symbol: "TATAPOWER.NS" },
  { name: "Adani Ports and Special Economic Zone", symbol: "ADANIPORTS.NS" },
  { name: "Adani Enterprises", symbol: "ADANIENT.NS" },
];
let topStocksLoaded = true;

let topCryptos = [
  { name: "XRP USD", symbol: "XRP-USD" },
  { name: "USD Coin USD", symbol: "USDC-USD" },
  { name: "Solana USD", symbol: "SOL-USD" },
  { name: "TRON USD", symbol: "TRX-USD" },
  { name: "Wrapped TRON USD", symbol: "WTRX-USD" },
  { name: "Lido Staked ETH USD", symbol: "STETH-USD" },
  { name: "Dogecoin USD", symbol: "DOGE-USD" },
  { name: "Cardano USD", symbol: "ADA-USD" },
  { name: "Bitcoin Cash USD", symbol: "BCH-USD" },
  { name: "Lido wstETH USD", symbol: "WSTETH-USD" },
  { name: "Hyperliquid USD", symbol: "HYPE32196-USD" },
  { name: "USDS USD", symbol: "USDS33039-USD" },
];

let topBonds = [
  { name: "13 WEEK TREASURY BILL", symbol: "^IRX" },
  { name: "Treasury Yield 5 Years", symbol: "^FVX" },
  { name: "CBOE Interest Rate 10 Year T No", symbol: "^TNX" },
  { name: "Treasury Yield 30 Years", symbol: "^TYX" },
  { name: "2-Year T-Note Futures", symbol: "2YY=F" },
  { name: "10-Year T-Note Futures", symbol: "ZN=F" },
];




const feesData = {
  stocks: [
    {
      platform: "Zerodha",
      allInPercent: 0.35,
      breakdown: [
        "₹0 brokerage",
        "STT (0.1%)",
        "Exchange & SEBI charges",
        "GST on charges",
        "Stamp duty",
      ],
    },
    {
      platform: "Groww",
      allInPercent: 0.35,
      breakdown: [
        "₹0 brokerage",
        "STT (0.1%)",
        "Exchange & SEBI charges",
        "GST on charges",
        "Stamp duty",
      ],
    },
    {
      platform: "Upstox",
      allInPercent: 0.4,
      breakdown: [
        "Brokerage (₹20 or %)",
        "STT (0.1%)",
        "Exchange & SEBI charges",
        "GST on charges",
        "Stamp duty",
      ],
    },
  ],

  bonds: [
    {
      platform: "Zerodha",
      allInPercent: 0.1,
      breakdown: ["Platform fee", "GST (18%)"],
    },
    {
      platform: "Groww",
      allInPercent: 0.1,
      breakdown: ["Platform fee", "GST (18%)"],
    },
    {
      platform: "5paisa",
      allInPercent: 0.08,
      breakdown: ["Flat brokerage", "Exchange charges", "GST (18%)"],
    },
  ],

  crypto: [
    {
      platform: "CoinSwitch",
      allInPercent: 1.5,
      breakdown: ["Trading fee", "GST (18%)", "1% TDS"],
    },
    {
      platform: "WazirX",
      allInPercent: 0.2,
      breakdown: ["Trading fee", "GST (18%)", "1% TDS"],
    },
    {
      platform: "ZebPay",
      allInPercent: 1.8,
      breakdown: ["Trading fee", "GST (18%)", "1% TDS"],
    },
  ],
};

/* ============================= */
/* Utility: Format INR Currency */
/* ============================= */
function formatINR(value) {
  return value.toLocaleString("en-IN", {
    style: "currency",
    currency: "INR",
  });
}

/* ============================= */
/* Load Holdings from Database */
/* ============================= */
async function loadHoldings() {
  try {
    const res = await fetch(HOLDINGS_API);
    const holdings = await res.json();

    const tbody = document.getElementById("holdingsBody");
    tbody.innerHTML = "";

    holdings.forEach((h) => {
      const symbol = h.asset.symbol;

      const row = document.createElement("tr");
      row.setAttribute("data-symbol", symbol);

      row.innerHTML = `
        <td>${symbol}</td>
        <td>${h.asset.type}</td>
        <td>${h.quantity}</td>
        <td>${h.avgBuyPrice}</td>
        <td class="current-price">Loading...</td>
        <td class="market-value">Loading...</td>
        <td class="pl">Loading...</td>
        <td><button class="alert-btn" id="alert-btn-${symbol}" onclick="toggleAlertForAsset('${symbol}')">Set Alert</button></td>
      `;

      tbody.appendChild(row);
    });

    updatePrices();
    updateAlertButtons();
  } catch (err) {
    console.error("Error loading holdings:", err);
  }
}

/* ============================= */
/* Fetch Live Prices + Update Table */
/* ============================= */
async function updatePrices() {
  const rows = document.querySelectorAll("#holdingsBody tr");
  const symbols = Array.from(rows).map((r) => r.getAttribute("data-symbol"));

  if (symbols.length === 0) return;

  try {
    const res = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(symbols),
    });

    const data = await res.json();

    rows.forEach((row) => {
      const symbol = row.getAttribute("data-symbol");
      const currentPrice = data[symbol]?.price;

      const units = parseFloat(row.children[2].innerText);
      const buyPrice = parseFloat(row.children[3].innerText);

      if (currentPrice) {
        const marketValue = currentPrice * units;
        const pl = marketValue - buyPrice * units;

        // Update Table Values
        row.querySelector(".current-price").innerText = formatINR(currentPrice);
        row.querySelector(".market-value").innerText = formatINR(marketValue);

        const plCell = row.querySelector(".pl");
        plCell.innerText = formatINR(pl);

        // Profit / Loss Coloring
        if (pl >= 0) {
          plCell.classList.add("positive");
          plCell.classList.remove("negative");
        } else {
          plCell.classList.add("negative");
          plCell.classList.remove("positive");
        }
      }
    });

    // Update Dashboard After Prices Update
    updateDashboardTotals();
    updatePortfolioSummary();
    updateBestWorstAssets();
    updateDailyChange();
  } catch (err) {
    console.error("Error fetching prices:", err);
  }
}

/* ============================= */
/* Dashboard Totals by Asset Type */
/* ============================= */
function updateDashboardTotals() {
  const totals = {
    STOCK: 0,
    BOND: 0,
    CRYPTO: 0,
    CASH: 0,
  };

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const type = row.children[1].innerText;
    const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
    const pl = parseFloat(plText);

    if (!isNaN(pl)) {
      totals[type] += pl;
    }
  });

  document.getElementById("stocksStat").innerHTML =
    `${formatINR(totals.STOCK)}<span>Stocks</span>`;

  document.getElementById("bondsStat").innerHTML =
    `${formatINR(totals.BOND)}<span>Bonds</span>`;

  document.getElementById("cryptoStat").innerHTML =
    `${formatINR(totals.CRYPTO)}<span>Crypto</span>`;

  document.getElementById("cashStat").innerHTML =
    `${formatINR(totals.CASH)}<span>Cash</span>`;
}

/* ============================= */
/* Portfolio Total Value + Return */
/* ============================= */
function updatePortfolioSummary() {
  let totalMarketValue = 0;
  let totalInvested = 0;

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const units = parseFloat(row.children[2].innerText);
    const buyPrice = parseFloat(row.children[3].innerText);

    const currentText = row
      .querySelector(".current-price")
      .innerText.replace(/₹|,/g, "");
    const currentPrice = parseFloat(currentText);

    if (!isNaN(currentPrice)) {
      totalInvested += buyPrice * units;
      totalMarketValue += currentPrice * units;
    }
  });

  const totalProfit = totalMarketValue - totalInvested;

  const returnPercent =
    totalInvested > 0 ? (totalProfit / totalInvested) * 100 : 0;

  document.getElementById("portfolioValue").innerText =
    formatINR(totalMarketValue);

  const returnEl = document.getElementById("portfolioReturn");
  returnEl.innerText = `${returnPercent >= 0 ? "+" : ""}${returnPercent.toFixed(2)}%`;

  // Color Return
  returnEl.className = returnPercent >= 0 ? "positive" : "negative";
}

/* ============================= */
/* Best + Worst Performing Asset */
/* ============================= */
function updateBestWorstAssets() {
  let best = { symbol: "-", profit: -Infinity };
  let worst = { symbol: "-", profit: Infinity };

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const symbol = row.children[0].innerText;

    const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
    const profit = parseFloat(plText);

    if (!isNaN(profit)) {
      if (profit > best.profit) best = { symbol, profit };
      if (profit < worst.profit) worst = { symbol, profit };
    }
  });

  document.getElementById("bestAsset").innerHTML = `
    <span>Best Performer</span>
    <div class="value">${best.symbol}</div>
    <strong class="positive">${formatINR(best.profit)}</strong>
  `;

  document.getElementById("worstAsset").innerHTML = `
    <span>Worst Performer</span>
    <div class="value">${worst.symbol}</div>
    <strong class="negative">${formatINR(worst.profit)}</strong>
  `;
}

/* ============================= */
/* Daily Change (Total P/L Today) */
/* ============================= */
function updateDailyChange() {
  let totalPL = 0;

  document.querySelectorAll("#holdingsBody tr").forEach((row) => {
    const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
    const profit = parseFloat(plText);

    if (!isNaN(profit)) totalPL += profit;
  });

  document.getElementById("dailyChange").innerHTML = `
    <span>Daily Change</span>
    <div class="value">${formatINR(totalPL)}</div>
  `;

  document.getElementById("dailyChange").className =
    totalPL >= 0 ? "stat positive" : "stat negative";
}

window.addEventListener("load", () => {
  loadHoldings();
});

setInterval(updatePrices, REFRESH_INTERVAL);
function openAssetModal() {
  resetModalFields();
  document.getElementById("assetModal").style.display = "flex";
}

function closeAssetModal() {
  document.getElementById("assetModal").style.display = "none";
  resetModalFields();
}

// function updateAssetOptions() {
//   const type = document.getElementById("assetType").value;
//   const category = document.getElementById("assetCategory");

//   category.innerHTML = "";

//   if (type === "cash") {
//     category.innerHTML = `<option>INR Cash</option>`;
//   }

//   if (type === "stock") {
//     category.innerHTML = `
//       <option>Large Cap</option>
//       <option>Mid Cap</option>
//       <option>Small Cap</option>
//     `;
//   }

//   if (type === "crypto") {
//     category.innerHTML = `
//       <option>Bitcoin</option>
//       <option>Ethereum</option>
//       <option>Altcoin</option>
//     `;
//   }
// }

const tabs = document.querySelectorAll(".asset-tab");
const feesGrid = document.getElementById("feesGrid");

function renderFees(asset) {
  feesGrid.innerHTML = "";

  feesData[asset].forEach((item) => {
    feesGrid.innerHTML += `
      <div class="fee-card">
        <h4>${item.platform}</h4>
        <span>${asset.toUpperCase()}</span>

        <div class="fee-percent">
          ~${item.allInPercent}%
        </div>

        <div class="fee-breakdown">
          ${item.breakdown.map((b) => `• ${b}`).join("<br>")}
        </div>
      </div>
    `;
  });
}

tabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    tabs.forEach((t) => t.classList.remove("active"));
    tab.classList.add("active");
    renderFees(tab.dataset.asset);
  });
});

// initial load
renderFees("stocks");

// Navigation
const navButtons = document.querySelectorAll(".nav");
const views = document.querySelectorAll(".view");

navButtons.forEach((button) => {
  button.addEventListener("click", () => {
    // Remove active from all nav and views
    navButtons.forEach((btn) => btn.classList.remove("active"));
    views.forEach((view) => view.classList.remove("active"));

    // Add active to clicked nav and corresponding view
    button.classList.add("active");
    const viewId = button.dataset.view + "-view";
    document.getElementById(viewId).classList.add("active");
  });
});

let currentFeePercent = 0;

function updateFees() {
  const assetType = document.getElementById("assetType").value;
  const platform = document.getElementById("platform").value;

  let assetKey = assetType;
  if (assetType === "stock") assetKey = "stocks";
  if (assetType === "cash") {
    currentFeePercent = 0;
    document.getElementById("platformFeeDetails").innerHTML =
      "No fees for cash";
    updateBill();
    return;
  }

  const data = feesData[assetKey];
  if (data) {
    const platformData = data.find(
      (p) => p.platform.toLowerCase() === platform,
    );
    if (platformData) {
      currentFeePercent = platformData.allInPercent;
      document.getElementById("platformFeeDetails").innerHTML = `
        <div class="fee-percent">~${platformData.allInPercent}%</div>
        <div class="fee-breakdown">${platformData.breakdown.map((b) => `• ${b}`).join("<br>")}</div>
      `;
    } else {
      currentFeePercent = 0;
      document.getElementById("platformFeeDetails").innerHTML =
        "No fees data available for this platform";
    }
  } else {
    currentFeePercent = 0;
    document.getElementById("platformFeeDetails").innerHTML =
      "Select an asset type and platform";
  }
  updateBill();
}

function updateBill() {
  const units = parseFloat(document.getElementById("units").value) || 0;
  const pricePerUnit =
    parseFloat(document.getElementById("pricePerUnit").value) || 0;

  const totalInvestment = units * pricePerUnit;
  const fees = totalInvestment * (currentFeePercent / 100);
  const totalCost = totalInvestment + fees;

  document.getElementById("totalInvestment").innerText =
    formatINR(totalInvestment);
  document.getElementById("fees").innerText = formatINR(fees);
  document.getElementById("totalCost").innerText = formatINR(totalCost);
}



function updateAssetOptions() {
  const type = document.getElementById("assetType").value;
  const category = document.getElementById("assetCategory");
  const platform = document.getElementById("platform");

  category.innerHTML = "";
  platform.innerHTML = "";

  if (type === "cash") {
    category.innerHTML = `<option>INR Cash</option>`;
    platform.innerHTML = `<option value="none">No Platform</option>`;
  }

  if (type === "stock") {
    category.innerHTML = topStocks
      .map((stock) => `<option value="${stock.symbol}">${stock.name}</option>`)
      .join("");
    platform.innerHTML = `
      <option value="zerodha">Zerodha</option>
      <option value="groww">Groww</option>
      <option value="upstox">Upstox</option>
    `;
  }

  if (type === "crypto") {
    category.innerHTML = topCryptos
      .map(
        (crypto) => `<option value="${crypto.symbol}">${crypto.name}</option>`,
      )
      .join("");
    platform.innerHTML = `
      <option value="coinswitch">CoinSwitch</option>
      <option value="wazirx">WazirX</option>
      <option value="zebpay">ZebPay</option>
    `;
  }

  if (type === "bonds") {
    category.innerHTML = topBonds
      .map((bond) => `<option value="${bond.symbol}">${bond.name}</option>`)
      .join("");
    platform.innerHTML = `
      <option value="zerodha">Zerodha</option>
      <option value="groww">Groww</option>
      <option value="5paisa">5paisa</option>
    `;
  }

  updateFees();
}

/* ============================= */
/* Sell Modal Functions */
/* ============================= */
function openSellModal() {
  document.getElementById("sellModal").style.display = "flex";
  loadSellAssets();
}

function closeSellModal() {
  document.getElementById("sellModal").style.display = "none";
}

async function loadSellAssets() {
  try {
    const res = await fetch(HOLDINGS_API);
    const holdings = await res.json();
    
    const select = document.getElementById("sellAssetSelect");
    select.innerHTML = '<option value="">Select asset</option>';
    
    holdings.forEach(h => {
      const option = document.createElement("option");
      option.value = h.asset.symbol;
      option.textContent = `${h.asset.symbol} - ${h.asset.name} (${h.quantity} units)`;
      option.dataset.units = h.quantity;
      option.dataset.currentPrice = h.currentPrice;
      select.appendChild(option);
    });
  } catch (err) {
    console.error("Error loading assets for sell:", err);
  }
}

function updateSellInfo() {
  const select = document.getElementById("sellAssetSelect");
  const selectedOption = select.options[select.selectedIndex];
  
  if (selectedOption.value) {
    document.getElementById("sellAssetInfo").style.display = "block";
    document.getElementById("currentUnits").textContent = selectedOption.dataset.units;
    document.getElementById("currentPrice").textContent = selectedOption.dataset.currentPrice;
    
    // Set max for sell units input
    document.getElementById("sellUnits").max = selectedOption.dataset.units;
  } else {
    document.getElementById("sellAssetInfo").style.display = "none";
  }
  
  updateSellSummary();
}

function updateSellSummary() {
  const select = document.getElementById("sellAssetSelect");
  const selectedOption = select.options[select.selectedIndex];
  const sellUnits = parseFloat(document.getElementById("sellUnits").value) || 0;
  
  if (selectedOption.value && sellUnits > 0) {
    const currentPrice = parseFloat(selectedOption.dataset.currentPrice);
    const marketValue = currentPrice * sellUnits;
    document.getElementById("sellMarketValue").textContent = formatINR(marketValue);
  } else {
    document.getElementById("sellMarketValue").textContent = "₹0";
  }
}

async function sellAsset() {
  const symbol = document.getElementById("sellAssetSelect").value;
  const quantity = parseFloat(document.getElementById("sellUnits").value);
  
  if (!symbol || !quantity) {
    alert("Please select an asset and enter quantity to sell!");
    return;
  }
  
  try {
    const payload = {
      symbol: symbol,
      quantity: quantity
    };
    
    const res = await fetch("http://127.0.0.1:8080/api/holdings/sell", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    
    if (!res.ok) {
      throw new Error("Sell transaction failed!");
    }
    
    alert("✅ Asset Sold Successfully!");
    closeSellModal();
    loadHoldings();
    
  } catch (err) {
    console.error("Error selling asset:", err);
    alert("❌ Error selling asset!");
  }
}

/* ============================= */
/* Load Assets from Database */
/* ============================= */
async function loadAssetsFromDatabase() {
  try {
    const res = await fetch("http://127.0.0.1:8080/api/assets");
    const assets = await res.json();
    
    // Store assets globally for use in updateAssetOptions
    window.databaseAssets = assets;
    
  } catch (err) {
    console.error("Error loading assets from database:", err);
  }
}

// Update the existing updateAssetOptions function to use database assets
function updateAssetOptions() {
  const type = document.getElementById("assetType").value;
  const category = document.getElementById("assetCategory");
  const platform = document.getElementById("platform");

  category.innerHTML = "";
  platform.innerHTML = "";

  if (type === "cash") {
    category.innerHTML = `<option>INR Cash</option>`;
    platform.innerHTML = `<option value="none">No Platform</option>`;
  } else if (window.databaseAssets) {
    // Filter assets by type from database
    const filteredAssets = window.databaseAssets.filter(asset => 
      asset.type.toLowerCase() === type.toLowerCase()
    );
    
    filteredAssets.forEach(asset => {
      const option = document.createElement("option");
      option.value = asset.symbol;
      option.textContent = `${asset.name} (${asset.symbol})`;
      category.appendChild(option);
    });
    
    // Add platform options based on asset type
    if (type === "stock") {
      platform.innerHTML = `
        <option value="zerodha">Zerodha</option>
        <option value="groww">Groww</option>
        <option value="upstox">Upstox</option>
      `;
    } else if (type === "crypto") {
      platform.innerHTML = `
        <option value="coinswitch">CoinSwitch</option>
        <option value="wazirx">WazirX</option>
        <option value="zebpay">ZebPay</option>
      `;
    } else if (type === "bonds") {
      platform.innerHTML = `
        <option value="zerodha">Zerodha</option>
        <option value="groww">Groww</option>
        <option value="5paisa">5paisa</option>
      `;
    }
  } else {
    // Fallback to hardcoded assets if database not loaded
    if (type === "stock") {
      category.innerHTML = topStocks
        .map((stock) => `<option value="${stock.symbol}">${stock.name}</option>`)
        .join("");
      platform.innerHTML = `
        <option value="zerodha">Zerodha</option>
        <option value="groww">Groww</option>
        <option value="upstox">Upstox</option>
      `;
    }
    // ... rest of the existing logic for crypto and bonds
  }

  updateFees();
}

// Load assets when page loads
window.addEventListener("load", () => {
  loadAssetsFromDatabase();
  loadHoldings();
  loadNotifications();
  updateNotificationBadge();
  loadAIInsights();
});

// Periodically refresh AI insights
setInterval(loadAIInsights, AI_INSIGHTS_REFRESH_INTERVAL);

/* ============================= */
/* Price Alert Functions */
/* ============================= */
function toggleAlertFields() {
  const enableAlert = document.getElementById("enableAlert").checked;
  const alertFields = document.getElementById("alertFields");
  alertFields.style.display = enableAlert ? "block" : "none";
}

function resetModalFields() {
  document.getElementById("assetType").value = "";
  document.getElementById("assetCategory").innerHTML = '<option value="">Select asset type first</option>';
  document.getElementById("platform").innerHTML = '';
  document.getElementById("units").value = "";
  document.getElementById("pricePerUnit").value = "";
  document.getElementById("enableAlert").checked = false;
  document.getElementById("alertFields").style.display = "none";
  document.getElementById("thresholdPrice").value = "";
  document.getElementById("alertCondition").value = "ABOVE";
  updateBill();
}

function resetAlertModalFields() {
  document.getElementById("alertAssetSelect").value = "";
  document.getElementById("thresholdPriceModal").value = "";
  document.getElementById("alertConditionModal").value = "ABOVE";
}

async function saveAsset() {
  // ✅ Selected Symbol from dropdown
  const symbol = document.getElementById("assetCategory").value;

  const quantity = parseFloat(document.getElementById("units").value);
  const price = parseFloat(document.getElementById("pricePerUnit").value);
  const platform = document.getElementById("platform").value;

  if (!symbol || !quantity || !price) {
    alert("Please fill all fields correctly!");
    return;
  }

  // Fees Calculation
  const fees = quantity * price * (currentFeePercent / 100);
  const totalCost = quantity * price + fees;

  // ✅ Payload sent to backend
  const payload = {
    symbol: symbol,
    quantity: quantity,
    pricePerUnit: price,
    platform: platform,
    fees: fees,
    totalCost: totalCost
  };

  try {
    const res = await fetch("http://127.0.0.1:8080/api/transactions/buy", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      throw new Error("Transaction failed!");
    }

    // Save alert if enabled
    if (document.getElementById("enableAlert").checked) {
      await saveAlertForAsset(symbol);
    }

    alert("✅ Asset Bought Successfully!");

    closeAssetModal();
    loadHoldings();

  } catch (err) {
    console.error("Error saving transaction:", err);
    alert("❌ Error saving transaction!");
  }
}

async function saveAlertForAsset(symbol) {
  try {
    // Get asset ID from symbol
    const assetsRes = await fetch("http://127.0.0.1:8080/api/assets");
    const assets = await assetsRes.json();
    const asset = assets.find(a => a.symbol === symbol);
    
    if (!asset) {
      console.error("Asset not found for symbol:", symbol);
      return;
    }

    const alertPayload = {
      assetId: asset.id,
      thresholdPrice: parseFloat(document.getElementById("thresholdPrice").value),
      condition: document.getElementById("alertCondition").value
    };

    const alertRes = await fetch(ALERTS_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(alertPayload),
    });

    if (!alertRes.ok) {
      throw new Error("Alert creation failed!");
    }

    console.log("Alert created successfully");
  } catch (err) {
    console.error("Error creating alert:", err);
  }
}

/* ============================= */
/* Set Alert Modal Functions */
/* ============================= */
function openSetAlertModal() {
  resetAlertModalFields();
  document.getElementById("setAlertModal").style.display = "flex";
  loadAlertAssets();
}

function closeSetAlertModal() {
  document.getElementById("setAlertModal").style.display = "none";
  resetAlertModalFields();
}

async function loadAlertAssets() {
  try {
    const res = await fetch(HOLDINGS_API);
    const holdings = await res.json();
    
    const select = document.getElementById("alertAssetSelect");
    select.innerHTML = '<option value="">Select asset</option>';
    
    holdings.forEach(h => {
      const option = document.createElement("option");
      option.value = h.asset.id;
      option.textContent = `${h.asset.symbol} - ${h.asset.name}`;
      select.appendChild(option);
    });
  } catch (err) {
    console.error("Error loading assets for alert:", err);
  }
}

async function saveAlert() {
  const assetId = document.getElementById("alertAssetSelect").value;
  const thresholdPrice = parseFloat(document.getElementById("thresholdPriceModal").value);
  const condition = document.getElementById("alertConditionModal").value;

  if (!assetId || !thresholdPrice) {
    alert("Please select an asset and enter threshold price!");
    return;
  }

  console.log("Creating alert:", { assetId, thresholdPrice, condition });

  try {
    const payload = {
      assetId: parseInt(assetId),
      thresholdPrice: thresholdPrice,
      condition: condition
    };
    
    console.log("Alert payload:", payload);
    
    const res = await fetch(ALERTS_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    
    if (!res.ok) {
      throw new Error("Alert creation failed!");
    }

    const createdAlert = await res.json();
    console.log("Alert created successfully:", createdAlert);

    alert("Alert Set Successfully!");
    closeSetAlertModal();
    updateAlertButtons(); // Add this line
    
  } catch (err) {
    console.error("Error setting alert:", err);
    alert("Error setting alert!");
  }
}

/* ============================= */
/* Notification Center Functions */
/* ============================= */
function toggleNotificationCenter() {
  const notificationCenter = document.getElementById("notificationCenter");
  const isVisible = notificationCenter.style.display !== "none";
  
  if (isVisible) {
    notificationCenter.style.display = "none";
  } else {
    notificationCenter.style.display = "block";
    loadNotifications();
  }
}

async function loadNotifications() {
  try {
    const res = await fetch(NOTIFICATIONS_API);
    const notifications = await res.json();
    
    const notificationList = document.getElementById("notificationList");
    notificationList.innerHTML = "";
    
    if (notifications.length === 0) {
      notificationList.innerHTML = "<div class='no-notifications'>No notifications</div>";
      return;
    }
    
    notifications.forEach(notification => {
      const profitLoss = getProfitLossForAsset(notification.asset.symbol);
      const isProfit = profitLoss >= 0;
      const profitLossClass = isProfit ? 'positive' : 'negative';

      const notificationEl = document.createElement("div");
      notificationEl.className = `notification-item ${!notification.isRead ? 'unread' : ''}`;
      notificationEl.innerHTML = `
        <div class="notification-content">
          <div class="notification-message">${notification.message}</div>
          <div class="notification-meta">
            <span class="notification-time">${new Date(notification.createdAt).toLocaleString()}</span>
            <span class="notification-price ${profitLossClass}">P/L: ${formatINR(profitLoss)}</span>
          </div>
        </div>
        <div class="notification-actions">
          ${!notification.isRead ? `<button class="mark-read-btn" onclick="markNotificationAsRead(${notification.id})">Mark as read</button>` : ''}
          <button class="delete-btn" onclick="deleteNotification(${notification.id})">Delete</button>
        </div>
      `;
      notificationList.appendChild(notificationEl);
    });
    
    // Add clear all button if there are notifications
    if (notifications.length > 0) {
      const clearAllBtn = document.createElement("button");
      clearAllBtn.className = "clear-all-btn";
      clearAllBtn.textContent = "Clear All Notifications";
      clearAllBtn.onclick = clearAllNotifications;
      notificationList.appendChild(clearAllBtn);
    }
    
  } catch (err) {
    console.error("Error loading notifications:", err);
  }
}

async function markNotificationAsRead(notificationId) {
  try {
    const res = await fetch(`${NOTIFICATIONS_API}/${notificationId}/read`, {
      method: "PATCH"
    });
    
    if (res.ok) {
      loadNotifications();
      updateNotificationBadge();
    }
  } catch (err) {
    console.error("Error marking notification as read:", err);
  }
}

async function markAllNotificationsAsRead() {
  try {
    const res = await fetch(`${NOTIFICATIONS_API}/mark-all-read`, {
      method: "PATCH"
    });
    
    if (res.ok) {
      loadNotifications();
      updateNotificationBadge();
    }
  } catch (err) {
    console.error("Error marking all notifications as read:", err);
  }
}

async function updateNotificationBadge() {
  try {
    const res = await fetch(`${NOTIFICATIONS_API}/unread-count`);
    const data = await res.json();
    const count = data.count;
    
    const badge = document.getElementById("notificationBadge");
    if (count > 0) {
      badge.textContent = count > 99 ? "99+" : count;
      badge.style.display = "block";
    } else {
      badge.style.display = "none";
    }
  } catch (err) {
    console.error("Error updating notification badge:", err);
  }
}

async function deleteNotification(notificationId) {
  try {
    const res = await fetch(`${NOTIFICATIONS_API}/${notificationId}`, {
      method: 'DELETE'
    });
    
    if (res.ok) {
      loadNotifications();
      updateNotificationBadge();
    }
  } catch (err) {
    console.error("Error deleting notification:", err);
  }
}

async function clearAllNotifications() {
  if (confirm("Are you sure you want to clear all notifications?")) {
    try {
      const res = await fetch(`${NOTIFICATIONS_API}/clear-all`, {
        method: 'DELETE'
      });
      
      if (res.ok) {
        loadNotifications();
        updateNotificationBadge();
      }
    } catch (err) {
      console.error("Error clearing notifications:", err);
    }
  }
}

function getProfitLossForAsset(symbol) {
  const row = document.querySelector(`#holdingsBody tr[data-symbol="${symbol}"]`);
  if (!row) return 0;
  
  const plText = row.querySelector(".pl").innerText.replace(/₹|,/g, "");
  return parseFloat(plText) || 0;
}

// WebSocket or polling for new notifications
let lastNotificationCount = 0;
let lastNotificationIds = new Set();
async function checkForNewNotifications() {
  try {
    const res = await fetch(`${NOTIFICATIONS_API}/unread`);
    const notifications = await res.json();
    
    // Check for new notifications by comparing IDs
    const currentNotificationIds = new Set(notifications.map(n => n.id));
    const newNotifications = notifications.filter(n => !lastNotificationIds.has(n.id));
    
    if (newNotifications.length > 0) {
      // Show popup for each new notification
      newNotifications.forEach(notification => {
        showNotificationPopup(notification);
      });
      lastNotificationIds = currentNotificationIds;
    }
    
    updateNotificationBadge();
  } catch (err) {
    console.error("Error checking for new notifications:", err);
  }
}

function showNotificationPopup(notification) {
  const profitLoss = getProfitLossForAsset(notification.asset.symbol);
  const isProfit = profitLoss >= 0;
  const profitLossClass = isProfit ? 'positive' : 'negative';
  
  // Create popup element
  const popup = document.createElement("div");
  popup.className = "notification-popup";
  
  // Calculate position for multiple popups
  const existingPopups = document.querySelectorAll('.notification-popup');
  const offset = existingPopups.length * 120; // Stack popups vertically
  
  popup.style.top = `${80 + offset}px`;
  
  popup.innerHTML = `
    <div class="popup-content">
      <div class="popup-header">
        <h4>Price Alert</h4>
        <button class="popup-close" onclick="this.parentElement.parentElement.remove()">×</button>
      </div>
      <div class="popup-body">
        <p>${notification.message}</p>
        <div class="popup-meta">
          <span class="${profitLossClass}">P/L: ${formatINR(profitLoss)}</span>
          <span>${new Date(notification.createdAt).toLocaleString()}</span>
        </div>
      </div>
    </div>
  `;
  
  // Add to page and auto-remove after 5 seconds
  document.body.appendChild(popup);
  setTimeout(() => {
    if (popup.parentElement) {
      popup.remove();
      // Reposition remaining popups
      repositionPopups();
    }
  }, 5000);
}

function repositionPopups() {
  const popups = document.querySelectorAll('.notification-popup');
  popups.forEach((popup, index) => {
    popup.style.top = `${80 + (index * 120)}px`;
  });
}

// Check for new notifications every 10 seconds
setInterval(checkForNewNotifications, 10000);

// Update notification badge
setInterval(updateNotificationBadge, 30000);

async function openSetAlertModalForAsset(symbol) {
  openSetAlertModal();
  
  // Select the asset in the dropdown
  setTimeout(() => {
    const select = document.getElementById("alertAssetSelect");
    for (let option of select.options) {
      if (option.textContent.includes(symbol)) {
        select.value = option.value;
        break;
      }
    }
  }, 100);
}

// Update the existing loadHoldings function to include alert buttons
const originalLoadHoldings = loadHoldings;
loadHoldings = async function() {
  await originalLoadHoldings();
  setTimeout(updateHoldingsTableWithAlerts, 100);
};

async function toggleAlertForAsset(symbol) {
  const button = document.getElementById(`alert-btn-${symbol}`);

  if (button.textContent === 'Set Alert') {
    openSetAlertModalForAsset(symbol);
  } else {
    // Stop the alert
    await stopAlertForAsset(symbol);
  }
}

async function stopAlertForAsset(symbol) {
  try {
    // Get asset ID from symbol
    const assetsRes = await fetch("http://127.0.0.1:8080/api/assets");
    const assets = await assetsRes.json();
    const asset = assets.find(a => a.symbol === symbol);

    if (!asset) {
      console.error("Asset not found for symbol:", symbol);
      return;
    }

    const res = await fetch(`http://127.0.0.1:8080/api/alerts/asset/${asset.id}`, {
      method: 'DELETE'
    });

    if (res.ok) {
      updateAlertButton(symbol, false);
      alert("✅ Alert Stopped Successfully!");
    }
  } catch (err) {
    console.error("Error stopping alert:", err);
    alert("❌ Error stopping alert!");
  }
}

function updateAlertButton(symbol, hasAlert) {
  const button = document.getElementById(`alert-btn-${symbol}`);
  if (button) {
    if (hasAlert) {
      button.textContent = 'Stop Alert';
      button.style.backgroundColor = '#6b7280'; // Grey color
      button.style.hover = '#4b5563';
    } else {
      button.textContent = 'Set Alert';
      button.style.backgroundColor = '#f59e0b'; // Orange color
      button.style.hover = '#d97706';
    }
  }
}

async function updateAlertButtons() {
  try {
    const res = await fetch("http://127.0.0.1:8080/api/alerts");
    const alerts = await res.json();

    const activeAlertSymbols = alerts.map(alert => alert.asset.symbol);

    // Update all alert buttons
    document.querySelectorAll('.alert-btn').forEach(btn => {
      const symbol = btn.getAttribute('onclick').match(/'([^']+)'/)[1];
      updateAlertButton(symbol, activeAlertSymbols.includes(symbol));
    });
  } catch (err) {
    console.error("Error updating alert buttons:", err);
  }
}

// Periodically update notification badge
setInterval(updateNotificationBadge, 30000);

// Debug function to test alert evaluation
async function testAlertEvaluation() {
  try {
    const res = await fetch("http://127.0.0.1:8080/api/alerts/test-evaluation", {
      method: 'POST'
    });
    
    if (res.ok) {
      const result = await res.text();
      console.log("Alert evaluation test result:", result);
      alert("Alert evaluation triggered! Check console for details.");
      // Refresh notifications after testing
      setTimeout(() => {
        loadNotifications();
        updateNotificationBadge();
      }, 2000);
    } else {
      const error = await res.text();
      console.error("Alert evaluation test failed:", error);
      alert("Test failed: " + error);
    }
  } catch (err) {
    console.error("Error testing alert evaluation:", err);
    alert("Error testing alert evaluation!");
  }
}

/* ============================= */
/* AI Insights Functions */
/* ============================= */
async function loadAIInsights() {
  try {
    const container = document.getElementById("aiInsightsContainer");
    
    // Show loading state
    container.innerHTML = `
      <div class="ai-card">
        <div class="ai-loading">
          <i class="fas fa-spinner fa-spin"></i> Generating insights...
        </div>
      </div>
    `;
    
    const res = await fetch(AI_INSIGHTS_API);
    
    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }
    
    const data = await res.json();
    const insights = data.insights || [];
    
    // Clear container
    container.innerHTML = "";
    
    if (insights.length === 0) {
      container.innerHTML = `
        <div class="ai-card">
          <div class="ai-loading">No insights available at the moment.</div>
        </div>
      `;
      return;
    }
    
    // Display insights
    insights.forEach((insight) => {
      const card = document.createElement("div");
      card.className = "ai-card";
      card.innerHTML = `<div class="ai-insight-text">${escapeHtml(insight)}</div>`;
      container.appendChild(card);
    });
    
  } catch (err) {
    console.error("Error loading AI insights:", err);
    const container = document.getElementById("aiInsightsContainer");
    container.innerHTML = `
      <div class="ai-card">
        <div class="ai-error">
          <i class="fas fa-exclamation-triangle"></i> 
          Unable to load AI insights. Please check your connection and try again.
        </div>
      </div>
    `;
  }
}

function escapeHtml(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}
